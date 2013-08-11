(ns incise.core
  (:use (hiccup core def page))
  (:require (compojure [route :refer [resources not-found]]
                       [core :refer [defroutes]])
            (ring.middleware [reload :refer [wrap-reload]]
                             [stacktrace :refer [wrap-stacktrace]])
            [dieter.core :refer [asset-pipeline]]
            [org.httpkit.server :refer [run-server]]))

(defroutes routes
  (resources "/")
  (not-found (html5 [:h1 "404"])))

(def app (-> routes
             (wrap-stacktrace)
             (wrap-reload)
             (asset-pipeline {:cache-mode :development
                              :engine :v8
                              :compress false})))

(defn getenv
  "A nice wrapper around System/getenv that allows a second argument to be
  passed in as the default."
  ([variable default] (or (System/getenv variable) default))
  ([variable] (getenv variable nil)))

(defn -main
  "Run the application."
  [& [port thread-count]]
  (run-server app {:port (or port (Integer. (getenv "PORT" "5000")))
                   :thread (or thread-count (Integer. (getenv "THREAD_COUNT" "4")))}))

(declare server)

(defn defserver
  "Start a server and bind the result to a var, 'server'."
  [& args]
  (alter-var-root #'server (fn [& _] (apply -main args))))
