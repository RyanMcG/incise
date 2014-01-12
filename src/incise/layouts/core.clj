(ns incise.layouts.core
  (:require [incise.parsers.parse]
            [incise.config :as conf])
  (:refer-clojure :exclude [get]))

(defonce layouts (atom {}))

(defn exists?
  "Check for the existance of a layout with the given name."
  [layout-with-name]
  (contains? @layouts (name layout-with-name)))

(defn get [layout-name & more]
  (apply @layouts (name layout-name) more))

(defn Parse->string
  [^incise.parsers.parse.Parse parse-data]
  (if-let [layout-key (:layout parse-data)]
    (if-let [layout-fn (get layout-key)]
      (layout-fn (conf/get) parse-data)
      (throw (ex-info (str "No layout function of with registered with key "
                           layout-key)
                      {:layouts @layouts})))
    (throw (ex-info (str "No layout key specified in given parse.")
                    {:layouts @layouts
                     :parse parse-data}))))

(defn register
  "Register a layout function to a shortname"
  [short-names layout-fn]
  (swap! layouts
         merge (zipmap (map name short-names)
                       (repeat layout-fn))))
