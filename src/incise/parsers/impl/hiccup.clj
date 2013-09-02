(ns incise.parsers.impl.hiccup
  (:require (incise.parsers [core :refer [register]]
                            [html :refer [html-parser]])
            [hiccup.compiler :refer [compile-html]]))

(register [:hiccup] (html-parser compile-html))
