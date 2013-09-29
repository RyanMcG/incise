(ns incise.parsers.html
  "Provides a function, html-parser, to create what could be considered the
   standard parser from a function which takes the body of a file as a string
   and returns html."
  (:require (incise.parsers [helpers :as help]
                            [core :refer [map->Parse]])
            [incise.layouts.core :refer [Parse->string]]
            [incise.config :as conf]
            [taoensso.timbre :refer [info]]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [clojure.java.io :refer [file reader]])
  (:import [java.io File]))

(defn File->Parse
  [to-html ^File file]
  (let [file-str (slurp file)
        parse-meta (edn/read-string file-str)
        content (to-html (second (s/split file-str #"\}" 2)))]
    (map->Parse (assoc parse-meta
                       :extension "/index.html"
                       :content content))))

(defn write-Parse
  [^incise.parsers.core.Parse parse-data]
  (let [out-file (file (str (conf/get :out-dir) File/separator
                             (help/Parse->path parse-data)))]
    (-> out-file
        (.getParentFile)
        (.mkdirs))
    (info "Writing" (.getPath out-file))
    (spit out-file (Parse->string parse-data))
    out-file))

(defn html-parser
  "Take a function that parses a string into HTML and returns an HTML parser.

   An HTML parser is a function which takes a file, reads it and writes it out
   as html to the proper place under public. If it is a page it should appear at
   the root, if it is a post it will be placed under a directory strucutre based
   on its date."
  [to-html]
  (comp write-Parse (partial File->Parse to-html)))
