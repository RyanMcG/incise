(ns incise.utils
  (:require [clojure.java.io :refer [file]])
  (:import [java.io File]))

(defn remove-prefix-from-path
  "Remove the given prefix from the given path."
  [prefix-file afile]
  (-> afile
      (file)
      (.getCanonicalPath)
      (subs (inc (count (.getCanonicalPath (file prefix-file)))))))

(defn directory? [^File afile] (.isDirectory afile))

(defn- gitignore-file? [^File file]
  (= (.getName file) ".gitignore"))

(defn delete-recursively
  "Delete a directory tree."
  [^File root]
  (when root
    (when (.isDirectory root)
      (doseq [file (remove gitignore-file? (.listFiles root))]
        (delete-recursively file)))
    (.delete root)))
