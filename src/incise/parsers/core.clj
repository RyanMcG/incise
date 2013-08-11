(ns incise.parsers.core
  (:require [clojure.string :as s]))

(def parsers (atom {}))

(defprotocol Parser
  "A parser"
  (run [this path]))

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name extensions)
                       (repeat parser))))

(defn extension [^java.io.File file]
  (last (s/split (.getName file) #"\.")))

(defn parse [^java.io.File file]
  (println (str "HEREYRERE " file))
  (run (@parsers (extension file)) (.getPath file)))
