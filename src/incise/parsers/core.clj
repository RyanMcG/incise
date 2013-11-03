(ns incise.parsers.core
  (:require [incise.parsers.helpers :refer [extension]]
            [taoensso.timbre :refer [info]])
  (:import [java.io File]))

(defrecord Parse [^String title
                  ^String extension
                  ^String content
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

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name (if (sequential? extensions)
                                   extensions
                                   [extensions]))
                       (repeat parser))))

(defn parse
  "Do all the work, parse the file and output it to the proper location."
  [^File handle]
  {:pre [(instance? File handle)]}
  (let [ext (extension handle)]
    (when (contains? @parsers ext)
      (info "Parsing" (.getPath handle))
      ((@parsers ext) handle))))
