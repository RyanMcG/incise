(ns incise.core
  (:require [incise.server :refer [serve stop-server defserver]]))

(defn -main
  "Start the development server and watcher."
  [& args]
  (apply serve args))

(defn restart
  "Define a server and set up a watcher AT THE SAME TIME!"
  [& args]
  (stop-server)
  (apply defserver args))
