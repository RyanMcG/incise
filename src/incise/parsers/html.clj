(ns incise.parsers.html
  "Provides a function, html-parser, to create what could be considered the
   standard parser from a function which takes the body of a file as a string
   and returns html."
  (:require (incise.parsers [helpers :as help]
                            [core :refer [map->Parse record-parse]])
            [incise.layouts.core :refer [Parse->string]]
            [incise.config :as conf]
            [taoensso.timbre :refer [info]]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [clojure.java.io :refer [file reader]])
  (:import [java.io File]))

(defn File->Parse
  [content-fn ^File file]
  (let [file-str (slurp file)
        parse-meta (edn/read-string file-str)
        content (content-fn (second (s/split file-str #"\}" 2)))
        parse-data (merge {:extension "/index.html"
                           :content content} parse-meta)
        parse-from-file (map->Parse parse-data)]
    (record-parse (.getCanonicalPath file) parse-from-file)
    parse-from-file))

(defn write-Parse
  [^incise.parsers.core.Parse parse-data]
  (let [out-file (file (conf/get :out-dir) (help/Parse->path parse-data))]
    (-> out-file
        (.getParentFile)
        (.mkdirs))
    (spit out-file (Parse->string parse-data))
    out-file))

(defn html-parser
  "Take a function that parses a string into HTML and returns a HTML parser.

   An HTML parser is a function which takes a file, reads it and writes it out
   as html to the proper place under public. If it is a page it should appear at
   the root, if it is a post it will be placed under a directory strucutre based
   on its date."
  [to-html]
  (fn [file]
    (let [parse (File->Parse to-html file)]
      (delay [(write-Parse parse)]))))
