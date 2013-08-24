(ns incise.server
  (:require (compojure [route :refer [resources not-found]]
                       [core :refer [defroutes]])
            (hiccup [page :refer [html5]])
            (ring.middleware [reload :refer [wrap-reload]]
                             [stacktrace :refer [wrap-stacktrace]])
            [dieter.core :refer [asset-pipeline]]
            [org.httpkit.server :refer [run-server]]))

(defroutes routes
  (resources "/")
  (not-found (html5 [:h1 "404"])))

(defn wrap-static-index [handler]
  (fn [{:keys [uri] :as request}]
    (handler (if (= (last uri) \/)
               (assoc request :uri (str uri "index.html"))
               request))))

(def app (-> routes
             (wrap-static-index)
             ; (wrap-reload :dirs ["src" "spec"])
             (wrap-stacktrace)
             (asset-pipeline {:cache-mode :development
                              :engine :rhino
                              :compress false})))

(defn getenv
  "A nice wrapper around System/getenv that allows a second argument to be
  passed in as the default."
  [variable & [default]]
  (or (System/getenv variable) default))

(defn serve
  "Start the development server"
  [& [port thread-count]]
  (run-server app {:port (or port
                             (Integer. (getenv "PORT" "5000")))
                   :thread (or thread-count
                               (Integer. (getenv "THREAD_COUNT" "4")))}))

(def server nil)

(defn defserver
  "Start a server and bind the result to a var, 'server'."
  [& args]
  (alter-var-root #'server (fn [& _] (apply serve args))))

(defn stop-server []
  (when server (server)))
