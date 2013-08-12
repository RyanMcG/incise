(ns incise.parsers.markdown
  (:require [incise.parsers.core :as pc]))

(defn parse
  "Parse markdown file returning a Parse record."
  [^java.io.File file]
  (do (println (.getPath file))
      ^pc/Parse (pc/map->Parse {:title "Yoyoyo"
                                :content "yoyoyo"})))

(pc/register [:md :markdown] parse)
