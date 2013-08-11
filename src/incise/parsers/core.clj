(ns incise.parsers.core
  (:require [clojure.string :as s]))

(def parsers
  "An atom containing a mapping of extensions (strings) to parse functions. A
   parse function takes a java.io.File object and returns a string (hopefully of
   html). This string should have meta attached like:

     {:title \"Some nice title\"
      ; All other meta only necessary for posts
      :date \"2013-08-11\"
      :tags [\"a\" \"vector\" \"of\" \"descriptive\" \"tags\"]
      :category \"singular category\"}"
  (atom {}))

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name extensions)
                       (repeat parser))))

(defn extension [^java.io.File file]
  (last (s/split (.getName file) #"\.")))

(defn parse [^java.io.File file]
  ((@parsers (extension file)) file))
