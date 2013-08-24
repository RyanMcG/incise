(ns incise.layouts.core
  (:require [incise.parsers.core]
            [incise.config :as conf])
  (:refer-clojure :exclude [get]))

(def layouts (atom {}))

(defn exists?
  "Check for the existance of a layout with the given name."
  [layout-with-name]
  (contains? @layouts (name layout-with-name)))

(defn get [layout-name]
  (@layouts (name layout-name)))

(defn Parse->string
  [^incise.parsers.core.Parse parse-data]
  ((get (:layout parse-data)) conf/get (:content parse-data)))

(defn register
  "Register a layout function to a shortname"
  [short-name layout-fn]
  (swap! layouts
         assoc (name short-name) layout-fn))
