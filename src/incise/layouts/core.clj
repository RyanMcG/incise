(ns incise.layouts.core
  (:refer-clojure :exclude [get]))

(def layouts (atom {}))

(defn exists?
  "Check for the existance of a layout with the given name."
  [layout-with-name]
  (contains? @layouts layout-with-name))

(defn get [& args]
  (apply clojure.core/get @layouts args))

(defn register
  "Register a layout function to a shortname"
  [short-name layout-fn]
  (swap! layouts
         assoc (str short-name) layout-fn))
