(ns incise.parsers.core
  (:require [incise.parsers.utils :refer [extension]]
            (incise [config :as conf]
                    [utils :refer [directory?]])
            [clojure.java.io :refer [file]]
            [taoensso.timbre :refer [info]])
  (:import [java.io File]))

;; A Parse is simply a record definition which may be optionally used by incise
;; parsers. All HTML parsers use it as an intermediate format which gets stored
;; in the parses atom so they can be accessed globally during the final parsing
;; step. This is particularly useful for generating tags and the like.
(defrecord Parse [^String title
                  ^String extension
                  content
                  ^String date
                  ^String layout
                  ^String path
                  ^clojure.lang.Seqable tags
                  ^String category])

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

(defonce ^{:doc "An atom containing a mapping of extensions (strings) to parse
                functions. A parse function takes a java.io.File and returns
                either a thunk or delay, which when invoked returns a sequence
                of files. This two step invocation is necessary to achieve
                features like tags."}
  parsers
  (atom {}))

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name (if (sequential? extensions)
                                   extensions
                                   [extensions]))
                       (repeat parser))))

(defn register-mappings
  "Takes a map of custom parser mappings and applies them to the parsers map."
  [mappings]
  (doseq [[function-key new-function-key] mappings]
    (register new-function-key (@parsers (name function-key)))))

(defn parse
  "Do all the work, parse the file and output it to the proper location."
  [^File handle]
  {:pre [(instance? File handle)]}
  (when-let [mappings (conf/get :custom-parser-mappings)]
    (register-mappings mappings))
  (let [ext (extension handle)
        current-parsers @parsers]
    (when (contains? current-parsers ext)
      (info "Parsing" (.getPath handle))
      ((current-parsers ext) handle))))

(defn input-file-seq
  "Returns a sequence of files (exclusing directories) from the input
  directory."
  []
  (->> (conf/get :in-dir)
       (file)
       (file-seq)
       (remove directory?)))

(defn- invoke-delay-or-function
  "Invoke the given delay or function."
  [delay-or-fn]
  {:pre [((some-fn fn? delay?) delay-or-fn)]}
  (condp #(%1 %2) delay-or-fn
    fn? (delay-or-fn)
    delay? (force delay-or-fn)))

(defn- log-file
  "Take a file and return it after logging its path with the given prefix."
  [prefix ^File a-file]
  (info prefix (.getPath a-file))
  a-file)

(defn parse-all
  "Parse all of the given files completely by calling parse on each one and
  invoking the parse result."
  [files]
  (->> files
       (map parse)
       (keep identity)
       (doall) ; Ensure that all files have been parsed.
       (mapcat invoke-delay-or-function)
       (map (partial log-file "Generated"))))

(defn parse-all-input-files
  "Completely parse all of the files from the input directory."
  []
  (parse-all (input-file-seq)))
