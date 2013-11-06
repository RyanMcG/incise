(ns incise.parsers.core
  (:require [incise.parsers.helpers :refer [extension]]
            [incise.config :as conf]
            [taoensso.timbre :refer [info]])
  (:import [java.io File]))

(defrecord Parse [^String title
                  ^String extension
                  content
                  ^String date
                  ^String layout
                  ^String path
                  ^clojure.lang.Seqable tags
                  ^String category])

(defonce ^{:doc "An atom containing a mapping of extensions (strings) to parse
                functions. A parse function takes a java.io.File and writes
                another file appropriately."}
  parsers
  (atom {}))

(defonce parses (atom {}))
(defn dissoc-parses [deleted-paths]
  (apply swap! parses dissoc deleted-paths))
(defn record-parse [canonical-path ^Parse a-parse]
  (swap! parses assoc canonical-path a-parse))

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name (if (sequential? extensions)
                                   extensions
                                   [extensions]))
                       (repeat parser))))

(defn register-mappings
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
      (let [parsed-file ((current-parsers ext) handle)]
        (info "Wrote" (.getPath parsed-file))
        parsed-file))))
