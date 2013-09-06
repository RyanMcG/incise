(ns ring.middleware.incise
  (:require [clojure.java.io :refer [file]]
            [ns-tracker.core :refer [ns-tracker]]
            (incise [utils :refer [delete-recursively]]
                    [load :refer [load-parsers-and-layouts]]
                    [config :as conf])
            [incise.parsers.core :refer [parse]])
  (:import [java.io File]))

(def ^:private file-modification-times (atom {}))

(defn- modified?
  "If file is not in atom or it's modification date has advanced."
  [^File a-file]
  (let [previous-modification-time (@file-modification-times a-file)
        last-modification-time (.lastModified a-file)]
    (swap! file-modification-times assoc a-file (.lastModified a-file))
    (or (nil? previous-modification-time)
        (< previous-modification-time last-modification-time))))

(defn wrap-incise-parse
  "Call parse on each modified file in the given dir with each request."
  [handler]
  (let [orig-out *out*
        orig-err *err*]
    (delete-recursively (file (conf/get :out-dir)))
    (fn [request]
      (binding [*out* orig-out
                *err* orig-err]
        (->> (conf/get :in-dir)
             (file)
             (file-seq)
             (filter modified?)
             (map parse)
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
      (when-not (empty? (modified-namespaces))
        (reset! file-modification-times {}))
      (handler request))))

(defn wrap-parsers-reload
  "Reload all parsers and layouts with each request."
  [handler]
  (fn [request]
    (load-parsers-and-layouts)
    (handler request)))

(defn wrap-incise
  [handler]
  (conf/load)
  (-> handler
      (wrap-incise-parse)
      (wrap-reset-modified-files-with-source-change)
      (wrap-parsers-reload)))
