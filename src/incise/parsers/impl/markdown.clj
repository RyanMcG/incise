(ns incise.parsers.impl.markdown
  (:require (incise.parsers [core :as pc]
                            [html :refer [html-parser]])
            [me.raynes.cegdown :as md]))

(def ^:dynamic *options* [:fenced-code-blocks
                          :autolinks
                          :quotes
                          :abbreviations
                          :tables
                          :definition-lists
                          :smarts
                          :smartypants])

(defn markdown-to-html [markdown-str]
  (md/to-html markdown-str *options*))

(pc/register [:md :markdown] (html-parser markdown-to-html))
