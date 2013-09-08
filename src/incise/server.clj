(ns incise.server
  (:require (compojure [route :refer [files not-found]]
                       [core :refer [defroutes]])
            (hiccup [page :refer [html5]])
            (incise [config :as conf]
                    [load :refer [load-parsers-and-layouts]])
            (ring.middleware [reload :refer [wrap-reload]]
                             [incise :refer [wrap-incise]]
                             [stacktrace :refer [wrap-stacktrace-web]])
            [stefon.core :refer [asset-pipeline]]
            [taoensso.timbre :refer [info error]]
            [clojure.stacktrace :refer [print-cause-trace]]
            [org.httpkit.server :refer [run-server]]))

(conf/load)
(defroutes routes
  (files "/" {:root (conf/get :out-dir)})
  (not-found (html5 [:h1 "404"])))

(defn wrap-static-index [handler]
  (fn [{:keys [uri] :as request}]
    (handler (if (= (last uri) \/)
               (assoc request :uri (str uri "index.html"))
               request))))

(defn wrap-log-exceptions [func & {:keys [bubble] :or {bubble true}}]
  "Log (i.e. print) exceptions received from the given function."
  (fn [& args]
    (try
      (apply func args)
      (catch Exception e
        (error (with-out-str (print-cause-trace e)))
        (when bubble (throw e))))))

(def app (-> routes
             (wrap-static-index)
             (wrap-reload :dirs ["src" "spec"])
             (wrap-incise)
             (wrap-log-exceptions)
             (wrap-stacktrace-web)
             (asset-pipeline)))

(defn getenv
  "A nice wrapper around System/getenv that allows a second argument to be
  passed in as the default."
  [variable & [default]]
  (or (System/getenv variable) default))

(defn serve
  "Start the development server"
  []
  (let [port (Integer. (conf/get :port (getenv "PORT" 5000)))]
    (info "Serving at"
          (str "http://" (.getCanonicalHostName
                           (java.net.InetAddress/getLocalHost)) \: port \/))
    (run-server app {:port port
                     :thread (Integer.
                               (conf/get :thread-count
                                         (getenv "THREAD_COUNT" 4)))})))

(defonce server (atom nil))

(defn defserver
  "Start a server and bind the result to a var, 'server'."
  [& args]
  (reset! server (apply serve args)))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)))
