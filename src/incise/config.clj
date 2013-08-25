(ns incise.config
  (:require [clojure.java.io :refer [reader file resource]]
            [clojure.edn :as edn])
  (:import [java.io PushbackReader])
  (:refer-clojure :exclude [assoc get]))

(def ^:private config (atom {}))

(defn load-config []
  (when-let [config-file (file (resource "incise.edn"))]
    (reset! config (edn/read (PushbackReader. (reader config-file))))))

(load-config)

(defn assoc [& args]
  (apply swap! config assoc args))

(defn get [& args]
  (if (empty? args)
    @config
    (apply @config args)))
