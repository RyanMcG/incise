(ns incise.parsers.parse
  (:require [incise.config :as conf]))

;; A Parse is simply a record definition which may be optionally used by incise
;; parsers. All HTML parsers use it as an intermediate format which gets stored
;; in the parses atom so they can be accessed globally during the final parsing
;; step. This is particularly useful for generating tags and the like.
(defrecord Parse [^String title
                  ^String extension
                  content
                  date
                  ^String layout
                  ^String path
                  ^clojure.lang.Seqable tags
                  ^String category])

(defn publish-parse?
  "Determine whether or not the given Parse should be publish or not."
  [^Parse parse-data]
  (or (conf/get :ignore-publish)
      (conf/serving?)
      (:publish parse-data true)))

(defonce ^{:doc "An atom containing a map of paths to parses generated from
                files at those paths. This atom is used to record parses created
                during in the first step of parsing. Recording should take place
                in the first step so that all parses have been recorded before
                delays are executed."}
  parses
  (atom {}))

(defn dissoc-parses
  "Disassociates the given collection of paths from parses. This is useful
  during development when a file is deleted from the input directory."
  [deleted-paths]
  (apply swap! parses dissoc deleted-paths))

(defn record-parse
  "Record the given parse at the given path in the parses atom."
  [canonical-path ^Parse a-parse]
  (swap! parses assoc canonical-path a-parse))
