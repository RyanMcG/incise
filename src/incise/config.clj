(ns incise.config
  (:require [clojure.java.io :refer [reader file resource]]
            [clojure.edn :as edn])
  (:import [java.io PushbackReader])
  (:refer-clojure :exclude [merge load assoc get]))

(def ^:private config (atom {}))

(defn load []
  (when-let [config-file (file (resource "incise.edn"))]
    (reset! config (edn/read (PushbackReader. (reader config-file))))))

(defn assoc [& more]
  (apply swap! config clojure.core/assoc more))

(defn get [& more]
  (if (empty? more)
    @config
    (apply @config more)))

(defn merge [& more]
  (apply swap! config
         clojure.core/merge more))
