(ns incise.core
  (:use (hiccup core def page))
  (:require (compojure [route :as route]
                       [core :refer :all])
            (ring.middleware [reload :refer [wrap-reload]]
                             [stacktrace :refer [wrap-stacktrace]])
            [dieter.core :refer [asset-pipeline]]
            [org.httpkit.server :refer [run-server]]))

(defroutes routes
  (route/resources "/")
  (route/not-found (html5 [:h1 "404"])))

(def app (-> routes
             (wrap-stacktrace)
             (wrap-reload)
             (asset-pipeline {:cache-mode :development
                              :engine :v8
                              :compress false})))

(defn -main
  "Run the application."
  [& [port thread-count]]
  (run-server {:port (or port (Integer. (read-config "PORT" "5000")))
               :thread (or thread-count (Integer. (read-config "THREAD_COUNT" "4")))}))

(declare server)

(defn defserver
  "Start a server and bind the result to a var, 'server'."
  [& args]
  (alter-var-root #'server (fn [& _] (apply -main args))))
