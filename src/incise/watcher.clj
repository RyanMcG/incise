(ns incise.watcher
  (:require [watchtower.core :refer [watcher rate file-filter extensions
                                     on-change]]))

(defn log-exceptions [func & args]
  "Log (i.e. print) exceptions received from the given function."
  (try
    (apply func args)
    (catch Exception e
      (println (.getMessage e)))))


(defn watch
  [change-fn]
  (watcher ["resources/posts/" "resources/pages/"]
           (rate 300)
           (on-change (partial log-exceptions change-fn))))

(def watching nil)

(defn start-watching [& args]
  (alter-var-root #'watching (fn [& _] (apply watch args))))

(defn stop-watching []
  (when watching
    (future-cancel watching)))
