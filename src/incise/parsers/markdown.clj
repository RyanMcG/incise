(ns incise.parsers.markdown
  (:require [incise.parsers.core :as pc]))

(defn parse
  "Parse markdown into html."
  [^java.io.File file]
  (spit "yay" (with-meta "<h1>Yoyoyo</h1>" {:title "Yoyoyo"})))

(pc/register [:md :markdown] parse)
