(ns incise.parsers.html
  "Provides a function, html-parser, to create what could be considered the
   standard parser from a function which takes the body of a file as a string
   and returns html."
  (:require (incise.parsers [utils :refer [meta->write-path]]
                            [core :refer [map->Parse record-parse]])
            [incise.layouts.core :refer [Parse->string]]
            [incise.config :as conf]
            [taoensso.timbre :refer [info]]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [clojure.java.io :refer [file reader]])
  (:import [java.io File]))

(defn- remove-trailing-index-html
  [path]
  (s/replace path #"/index\.html$" "/"))

(defn Parse->path [parse]
  (->> parse
       (:path)
       (remove-trailing-index-html)
       (str \/)))

(defn- meta-and-content->Parse
  "Combine the given meta data and content into a Parse."
  [parse-meta content]
  (let [meta-with-defaults (merge {:extension "/index.html"
                                   :content content} parse-meta)]
    (map->Parse
      (assoc meta-with-defaults
             :path (meta->write-path meta-with-defaults)))))

(defn File->Parse
  "Read in a file and turn it into a Parse instance."
  [content-fn ^File file]
  (let [file-str (slurp file)
        parse-meta (merge (conf/get :parse-defaults)
                          (edn/read-string file-str))
        content (content-fn (second (s/split file-str #"\}" 2)))]
    (meta-and-content->Parse parse-meta content)))

(defn write-Parse
  "Write the result of Parse->string to a file in the out-dir at the path
  specified in the given parse. Return the written File."
  [^incise.parsers.core.Parse parse-data]
  (let [out-file (file (conf/get :out-dir) (:path parse-data))]
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
      (record-parse (.getCanonicalPath file) parse)
      (delay [(write-Parse parse)]))))
