(ns ring.middleware.incise
  (:require [clojure.java.io :refer [file]]
            [clojure.set :refer [difference]]
            [ns-tracker.core :refer [ns-tracker]]
            (incise [utils :refer [delete-recursively directory?]]
                    [load :refer [load-parsers-and-layouts]]
                    [config :as conf])
            [incise.parsers.core :refer [input-file-seq parse-all
                                         dissoc-parses]])
  (:import [java.io File]))

(defonce ^{:private true
           :doc "Keeps track of modification times of files for modified?."}
  file-modification-times
  (atom {}))

(defn- modified?
  "If file is not in atom or it's modification date has advanced."
  [^File a-file]
  (let [previous-modification-time (@file-modification-times a-file)
        last-modification-time (.lastModified a-file)]
    (swap! file-modification-times assoc a-file (.lastModified a-file))
    (or (nil? previous-modification-time)
        (< previous-modification-time last-modification-time))))

(def paths-set
  "Keeps track of all parseable files. From the last invocation of
  reference-files."
  (atom #{}))

(defn reference-files
  "Pass files through with side effects. Call dissoc-parses on deleted paths."
  [files]
  (let [old-paths-set @paths-set
        new-paths-set (set (map (memfn getCanonicalPath) files))
        deleted-paths (difference old-paths-set new-paths-set)]
    (dissoc-parses deleted-paths)
    (reset! paths-set new-paths-set))
  files)

(defn wrap-incise-parse
  "Call parse on each modified file in the given dir with each request."
  [handler]
  (reset! file-modification-times {})
  (let [orig-out *out*
        orig-err *err*]
    (delete-recursively (file (conf/get :out-dir)))
    (fn [request]
      (binding [*out* orig-out
                *err* orig-err]
        (->> (input-file-seq)
             (reference-files)
             (filter modified?)
             (parse-all)
             (dorun)))
      (handler request))))

(defn wrap-reset-modified-files-with-source-change
  "An almost copy of wrap-reload, but instead of reloading modified files this
   ensurs that the next time parse is called all content files are reparsed.

   Takes the following options:
     :dirs - A list of directories that contain the source files.
             Defaults to [\"src\"]."
  [handler & [options]]
  (let [source-dirs (:dirs options ["src"])
        modified-namespaces (ns-tracker source-dirs)]
    (fn [request]
      (when (seq (modified-namespaces)) (reset! file-modification-times {}))
      (handler request))))

(defn wrap-parsers-reload
  "Reload all parsers and layouts with each request."
  [handler]
  (fn [request]
    (load-parsers-and-layouts)
    (handler request)))

(defn wrap-incise
  "A middleware which does everything necessary for incise. It loads parsers and
  layouts, and eagerly parses files. It even reparses files when Clojure source
  is changed (e.g. a layout is modified)."
  [handler]
  (conf/load)
  (-> handler
      (wrap-incise-parse)
      (wrap-reset-modified-files-with-source-change)
      (wrap-parsers-reload)))
