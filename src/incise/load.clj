(ns incise.load
  (:require [incise.config :as conf]
            [clojure.java.classpath :refer [classpath]]
            [clojure.tools.namespace.find :as ns-tools]))

(defn namespace-is-layout-or-parser?
  "Predicate to determine if the given symbol is a namespace for a layout or
   parser. It would be a namespace under incise.layouts or incise.parsers."
  [namespace-sym]
  (re-find #"incise\.(layouts|parsers)\.impl\..+" (str namespace-sym)))

(defn namespace-is-spec-or-test?
  "Predicate to determine if the given namespace is a spec or test."
  [namespace-sym]
  (re-find #"-(test|spec)$" (str namespace-sym)))

(defn find-parser-and-layout-symbols
  "Find symbols for namespaces on the classpath that are valid layouts and
   parsers."
  []
  (->> (classpath)
       (ns-tools/find-namespaces)
       (remove namespace-is-spec-or-test?)
       (filter namespace-is-layout-or-parser?)))

(defn load-parsers-and-layouts
  "Require with reload all namespaces under incise.layouts and incise.parsers."
  []
  (doseq [ns-sym (find-parser-and-layout-symbols)]
    (require :reload ns-sym)))

(defn load-all []
  (load-parsers-and-layouts)
  (conf/load))
