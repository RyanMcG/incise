(ns incise.parsers.impl.clj
  (:require (incise.parsers [core :refer [register]]
                            [html :refer [html-parser]])))

(defn clj-parse [code]
  (->> (str \( code \))
       (read-string)))

(register [:clj] (html-parser clj-parse))
