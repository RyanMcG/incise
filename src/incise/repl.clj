(ns incise.repl
  (:require [clojure.tools.namespace.repl :refer :all]
            [incise.server :refer [stop start]]
            [incise.config :as conf]
            [incise.core :refer :all]))

(defn start-server [& {:as more}]
  (conf/load)
  (conf/merge! {:method :serve
                :port 5000
                :thread-count 4} more)
  (start))

(defn restart-server
  "Stop a server if it is already started and start a new one."
  [& args]
  (stop)
  (require :reload 'incise.server)
  (apply start-server args))
