(ns incise.config
  (:require [clojure.java.io :refer [reader file resource]]
            [clojure.edn :as edn]
            [manners.victorian :refer [defmannerisms]])
  (:import [java.io PushbackReader])
  (:refer-clojure :exclude [merge load assoc get]))

(defonce ^:private config (atom {}))

(defn load []
  (when-let [config-file (file (resource "incise.edn"))]
    (reset! config (edn/read (PushbackReader. (reader config-file))))))

(defn assoc [& more]
  (apply swap! config clojure.core/assoc more))

(defmannerisms config
  [(comp string? :in-dir) "must have an input dir"]
  [(comp string? :out-dir) "must have an output dir"])

(defn get [& more]
  (if (empty? more)
    @config
    (apply @config more)))

(defn merge [& more]
  (apply swap! config
         clojure.core/merge more))
