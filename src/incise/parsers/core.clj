(ns incise.parsers.core
  (:require [incise.layouts.core :as layout]
            [incise.config :as conf]
            [incise.parsers.helpers :refer [extension]]
            [clj-time.core :as tm]
            [clojure.java.io :refer [file]]
            [clojure.string :as s])
  (:import [java.io File]))

(defrecord Parse [^String title
                  ^String extension
                  ^String content
                  ^String date
                  ^String layout
                  ^clojure.lang.Seqable tags
                  ^String category])

(defprotocol Inciseable
  ^Parse (parse [this] "Creates a more temporary String representation of
                        this.")
  ^File (incise [this ^Parse parse layout] "Write the appropriate file."))

(def parsers
  "An atom containing a mapping of extensions (strings) to parse functions. A
   parse function takes a java.io.File and returns a Parse."
  (atom {}))

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name extensions)
                       (repeat parser))))

(defn valid-parse?
  "Predicate to determin if the given parse is valid. A valid parse must have a
   layout and title specified. The title must be non-empty and the layout name
   must be valid (i.e. it corresponds to an existing layout function."
  [^Parse parse]
  (and (layout/exists? (:layout parse))
       ((complement empty?) (:title parse))))

(defn source->output
  "Turn the given file into a Parseable using the constructor registered to its
   extension."
  [^File handle]
  {:pre [(contains? @parsers (extension handle))]}
  (let [inciseable ((@parsers (extension handle)) handle)
        parse ^Parse (.parse inciseable)]
        (.incise inciseable parse (layout/get (:layout parse)))))
