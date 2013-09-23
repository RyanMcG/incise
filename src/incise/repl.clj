(ns incise.repl
  (:require [clojure.tools.namespace.repl :refer :all]
            [incise.server :refer [stop start]]
            [incise.config :as conf]
            [incise.core :refer :all]))

(def start-server (wrap-pre start conf/load))
(defn restart-server
  "Stop a server if it is already started and start a new one."
  [& args]
  (stop)
  (require :reload 'incise.server)
  (apply start-server args))
