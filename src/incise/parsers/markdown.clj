(ns incise.parsers.markdown
  (:require [incise.parsers.core :as pc]
            [incise.parsers.helpers :as ph]
            [incise.config :refer [config]]
            [markdown.core :as md])
  (:import [java.io File StringWriter]))

(defn md-file->string
  "Parse the given file as markdown returning html."
  [^File file]
  (let [output (StringWriter.)]
    (md/md-to-html-string file output)
    (str output)))

(defrecord Markdown [file]
  pc/Inciseable
  (parse [this]
    (pc/map->Parse {:content (md-file->string (:file this))}))
  (incise [this parse layout]
    (ph/Parse->path parse)
    (layout @config (:content parse))))

(pc/register [:md :markdown] ->Markdown)
