(ns incise.repl
  (:require [clojure.tools.namespace.repl :refer :all]
            [incise.server :refer [stop start]]
            [incise.config :as conf]
            [incise.core :refer :all]))

(defn start-server []
  (conf/load)
  (conf/assoc :method :serve)
  (start))

(defn restart-server
  "Stop a server if it is already started and start a new one."
  [& args]
  (stop)
  (require :reload 'incise.server)
  (apply start-server args))
