(ns incise.parsers.html
  (:require (incise.parsers [helpers :as help]
                            [core :refer [map->Parse]])
            [incise.layouts.core :refer [Parse->string]]
            [taoensso.timbre :refer [info]]
            [clojure.edn :as edn]
            [clojure.string :as s]
            [clojure.java.io :refer [reader]])
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
  (let [file-path (File. (str "resources/public/"
                              (help/Parse->path parse-data)))]
    (-> file-path
        (.getParentFile)
        (.mkdirs))
    (info "Writing" (.getPath file-path))
    (spit file-path (Parse->string parse-data))))

(defn html-parser
  "Take a function that parses a string into HTML and returns an HTML parser.

   An HTML parser is a function which takes a file, reads it and writes it out
   as html to the proper place under public. If it is a page it should appear at
   the root, if it is a post it will be placed under a directory strucutre based
   on its date."
  [to-html]
  (comp write-Parse (partial File->Parse to-html)))
