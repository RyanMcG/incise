(ns incise.server
  (:require (compojure [route :refer [resources not-found]]
                       [core :refer [defroutes]])
            (hiccup [page :refer [html5]])
            (incise [config :as conf]
                    [load :refer [load-parsers-and-layouts]])
            (ring.middleware [reload :refer [wrap-reload]]
                             [incise :refer [wrap-incise]]
                             [stacktrace :refer [wrap-stacktrace-web]])
            [stefon.core :refer [asset-pipeline]]
            [taoensso.timbre :refer [error]]
            [clojure.stacktrace :refer [print-cause-trace]]
            [org.httpkit.server :refer [run-server]]))

(defroutes routes
  (resources "/")
  (not-found (html5 [:h1 "404"])))

(defn wrap-static-index [handler]
  (fn [{:keys [uri] :as request}]
    (handler (if (= (last uri) \/)
               (assoc request :uri (str uri "index.html"))
               request))))

(defn wrap-log-exceptions [func]
  "Log (i.e. print) exceptions received from the given function."
  (fn [& args]
    (try
      (apply func args)
      (catch Exception e
        (error (with-out-str (print-cause-trace e)))
        (throw e)))))

(def app (-> routes
             (wrap-static-index)
             (wrap-reload :dirs ["src" "spec"])
             (wrap-incise)
             (wrap-log-exceptions)
             (wrap-stacktrace-web)
             (asset-pipeline {:cache-mode :development
                              :engine :v8
                              :compress false})))

(defn getenv
  "A nice wrapper around System/getenv that allows a second argument to be
  passed in as the default."
  [variable & [default]]
  (or (System/getenv variable) default))

(defn serve
  "Start the development server"
  [& [port thread-count]]
  (load-all)
  (run-server app {:port (or port
                             (Integer. (getenv "PORT" "5000")))
                   :thread (or thread-count
                               (Integer. (getenv "THREAD_COUNT" "4")))}))

(defonce server (atom nil))

(defn defserver
  "Start a server and bind the result to a var, 'server'."
  [& args]
  (reset! server (apply serve args)))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)))
