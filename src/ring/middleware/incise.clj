(ns ring.middleware.incise
  (:require [clojure.java.io :refer [file]]
            [incise.parsers.core :refer [parse]])
  (:import [java.io File]))

(defn- gitignore-file? [^File file]
  (= (.getName file) ".gitignore"))

(defn- delete-recursively
  "Delete a directory tree."
  [^File root]
  (when (.isDirectory root)
    (doseq [file (remove gitignore-file? (.listFiles root))]
      (delete-recursively file)))
  (.delete root))

(def ^:private file-modification-times (atom {}))

(defn- modified?
  "If file is not in atom or it's modification date has advanced."
  [^File a-file]
  (let [previous-modification-time (@file-modification-times a-file)
        last-modification-time (.lastModified a-file)]
    (swap! file-modification-times assoc a-file (.lastModified a-file))
    (or (nil? previous-modification-time)
        (< previous-modification-time last-modification-time))))

(defn wrap-incise
  "Call parse on each modified file in the given dir with each request."
  [handler & {:keys [in out]}]
  (let [orig-out *out*
        orig-err *err*]
    (delete-recursively (file out))
    (fn [request]
      (binding [*out* orig-out
                *err* orig-err]
        (->> in
             (file)
             (file-seq)
             (filter modified?)
             (map parse)
             (dorun)))
      (handler request))))
