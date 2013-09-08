(ns incise.utils
  (:import [java.io File]))

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
