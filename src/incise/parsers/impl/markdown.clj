(ns incise.parsers.impl.markdown
  (:require (incise.parsers [core :as pc]
                            [html :refer [html-parser]])
            [markdown.core :as md]))

(pc/register [:md :markdown] (html-parser md/md-to-html-string))
