(ns incise.load
  (:require [incise.config :as conf]
            [clojure.java.classpath :refer [classpath]]
            [clojure.tools.namespace.find :as ns-tools]))

(defn- namespace-is-layout-or-parser?
  "Predicate to determine if the given symbol is a namespace for a layout or
   parser."
  [namespace-sym]
  (re-find #"incise\.(layouts|parsers)\.impl\..+" (str namespace-sym)))

(defn- namespace-is-spec-or-test?
  "Predicate to determine if the given namespace is a spec or test."
  [namespace-sym]
  (re-find #"-(test|spec)$" (str namespace-sym)))

(defn- filter-namespaces
  "Find symbols for namespaces on the classpath that pass the given filter
   function."
  [filter-fn]
  (->> (classpath)
       (ns-tools/find-namespaces)
       (remove namespace-is-spec-or-test?)
       (filter filter-fn)))

(defn- load-ns-syms
  "Require with reload all namespaces returned by the given fn."
  [ns-syms-fn]
  (doseq [ns-sym (ns-syms-fn)]
    (require :reload ns-sym)))

(def load-parsers-and-layouts
  (partial load-ns-syms
           (partial filter-namespaces namespace-is-layout-or-parser?)))
