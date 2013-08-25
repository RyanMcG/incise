(ns incise.watcher
  (:require [taoensso.timbre :refer [error]]
            [clojure.stacktrace :refer [print-cause-trace]]
            [watchtower.core :refer [watcher rate file-filter extensions
                                     on-change]])
  (:import [java.io File]))

(defn log-exceptions [func]
  "Log (i.e. print) exceptions received from the given function."
  (let [out *out*
        err *err*]
    (fn [& args]
      (binding [*out* out
                *err* err]
        (try
          (apply func args)
          (catch Exception e
            (error (with-out-str (print-cause-trace e)))))))))

(defn per-change [change-fn]
  (fn [files]
    (doseq [file files]
      (change-fn file))))

(def gitignore-file? [^File file]
  (= (.getName file) ".gitignore"))

(defn delete-recursively
  "Delete a directory tree."
  [^File root]
  (when (.isDirectory root)
    (doseq [file (remove gitignore-file? (.listFiles root))]
      (delete-recursively file)))
  (.delete root))

(defn watch
  [change-fn]
  (delete-recursively (File. "resources/public/"))
  (watcher ["resources/posts/" "resources/pages/"]
           (rate 300)
           (on-change (-> change-fn
                          per-change
                          log-exceptions))))

(def watching nil)

(defn start-watching [& args]
  (alter-var-root #'watching (fn [& _] (apply watch args))))

(defn stop-watching []
  (when watching
    (future-cancel watching)))
