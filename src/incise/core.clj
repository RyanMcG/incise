(ns incise.core
  (:require (incise [server :refer [serve stop-server defserver]]
                    [watcher :refer [start-watching stop-watching]])
            [incise.parsers.core :refer [parse]]
            [clojure.java.classpath :refer [classpath]]
            [clojure.tools.namespace.find :as ns-tools]))

(def ^:const core-namespace-symbols
  "Namespaces which are the core parser and layouts."
  #{'incise.parsers.core
    'incise.layouts.core})

(defn namespace-is-layout-or-parser [namespace-sym]
  (and (not (contains? core-namespace-symbols namespace-sym))
       (re-find #"incise\.(layouts|parsers)\..+" (str namespace-sym))))

(defn load-parsers-and-layouts []
  (doseq [ns-sym (filter namespace-is-layout-or-parser
                         (ns-tools/find-namespaces (classpath)))]
    (require ns-sym)))

(load-parsers-and-layouts)

(def parse-on-watch (partial start-watching parse))

(defn -main
  "Start the development server and watcher."
  [& args]
  (apply serve args)
  (parse-on-watch))

(defn defserver-and-watch
  [& args]
  (stop-server)
  (apply defserver args)
  (stop-watching)
  (parse-on-watch))
