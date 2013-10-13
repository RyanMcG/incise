(ns incise.config
  (:require [clojure.java.io :refer [reader file resource]]
            [clojure.edn :as edn]
            [manners.victorian :refer [defmannerisms]])
  (:import [java.io PushbackReader])
  (:refer-clojure :exclude [merge load assoc get]))

(defonce config (atom {}))

(defn load []
  (when-let [config-file (file (resource "incise.edn"))]
    (reset! config (edn/read (PushbackReader. (reader config-file))))))

(defn assoc [& more]
  (apply swap! config clojure.core/assoc more))

(defmannerisms config
  [:in-dir "must have an in-dir"]
  [(comp string? :in-dir) "in-dir must be a string (like a path)"]
  [:out-dir "must have an output dir"]
  [(comp string? :out-dir) "out-dir must be a string (like a path)"])

(defn avow! [] (avow-config! @config))

(defn get [& more]
  (if (empty? more)
    @config
    (apply @config more)))

(defn merge [& more]
  (apply swap! config
         clojure.core/merge more))
