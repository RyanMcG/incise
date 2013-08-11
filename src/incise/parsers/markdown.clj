(ns incise.parsers.markdown
  (:require [incise.parsers.core :as pc]))

(defn- parse-from-path
  [file-path]
  (do (println (str "Parsing: " file-path))))

(defrecord Markdown []
  pc/Parser
  (run [this path]
    (parse-from-path path)))

(pc/register [:md :markdown] ->Markdown)
