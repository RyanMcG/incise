(ns incise.config
  (:require [clojure.java.io :refer [reader file resource]]
            [clojure.edn :as edn]
            [faulter.core :refer [faultless!]])
  (:import [java.io PushbackReader])
  (:refer-clojure :exclude [merge load assoc get]))

(def ^:private config (atom {}))

(defn load []
  (when-let [config-file (file (resource "incise.edn"))]
    (reset! config (edn/read (PushbackReader. (reader config-file))))))

(defn assoc [& more]
  (apply swap! config clojure.core/assoc more))

(def ^:private validations
  [[(comp string? :in-dir) "must have an input dir"]
   [(comp string? :out-dir) "must have an output dir"]])

(defn faultless-config!
  "Throws an AssertionError unless the given config map, or @config if not
  supplied, has no faults."
  [& [given-config]]
  (faultless! 'config validations (or given-config @config)))

(defn get [& more]
  (faultless-config!)
  (if (empty? more)
    @config
    (apply @config more)))

(defn merge [& more]
  (apply swap! config
         clojure.core/merge more))
