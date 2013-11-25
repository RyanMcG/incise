(ns incise.config
  (:require [clojure.java.io :refer [reader file resource]]
            [clojure.edn :as edn]
            [manners.victorian :refer [defmannerisms]])
  (:import [java.io PushbackReader])
  (:refer-clojure :exclude [merge load assoc get]))

(defonce config (atom {:in-dir "content"
                       :out-dir "public"}))

(defn get [& more]
  (if (empty? more)
    @config
    (apply @config more)))

(defn merge [& more]
  (apply swap! config
         clojure.core/merge more))

(defn assoc [& more]
  (apply swap! config clojure.core/assoc more))

(defonce config-path (atom nil))

(defn serving? [] (= (get :method) :serve))

(defn load
  "Load the config from "
  [& [path-to-config]]
  (when path-to-config (reset! config-path path-to-config))
  (when-let [config-file (file (or @config-path (resource "incise.edn")))]
    (merge (edn/read (PushbackReader. (reader config-file))))))

(defn- str-starts-or-ends-with-slash?
  [a-str]
  {:pre [(string? a-str)]}
  (some #(= \/ %) [(last a-str) (first a-str)]))

(defmannerisms config
  [(comp (some-fn nil? string?) :uri-root) "uri-root must be a string"]
  [(comp (complement (every-pred string? str-starts-or-ends-with-slash?))
         :uri-root)
   "uri-root must not start or end with a \"/\""]
  [:in-dir "must have an input directory (in-dir)"]
  [(comp string? :in-dir) "in-dir must be a string (like a path)"]
  [:out-dir "must have an output directory (out-dir)"]
  [(comp string? :out-dir) "out-dir must be a string (like a path)"])

(defn avow! [] (avow-config! @config))
