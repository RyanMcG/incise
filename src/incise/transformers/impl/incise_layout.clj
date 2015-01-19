(ns incise.transformers.impl.incise-layout
  (:require (incise.transformers [layout :refer [repartial use-layout
                                                 deflayout defpartial]]
                                 [core :refer [register]])
            [stefon.core :refer [link-to-asset]]
            [robert.hooke :refer [clear-hooks]]
            (incise.transformers.impl [vm-layout :as vm-layout]
                                      [base-layout :as base-layout])
            [hiccup.util :refer [to-uri]]))

(defpartial header []
  [:header
   [:h1
    [:a {:href "http://www.ryanmcg.com/incise/"
         :title "inciꞅe"}
     [:img {:alt "inciꞅe"
            :src "https://raw.github.com/RyanMcG/incise/master/website/content/assets/images/logo.png"}]]
    [:a {:href "https://travis-ci.org/RyanMcG/incise-core"
         :title "Build Status"}
     [:img {:alt "Build Status"
            :src "https://travis-ci.org/RyanMcG/incise-core.png?branch=master"}]]]
   [:span.tag-line "An extensible static site generator written in Clojure."]
   ; This nav is duplicated in README.md
   [:ul.nav
    [:li [:a {:href "https://github.com/RyanMcG/incise-core"
              :title "Source code on GitHub"}
          "Source"]]
    [:li
     [:a {:href "http://www.ryanmcg.com/incise/api/"
          :title "codox generated API documentation"}
      "API"]]
    [:li
     [:a {:href "http://www.ryanmcg.com/incise/extensibility/"
          :title "codox generated API documentation"}
      "Extensibility"]]]])

(defpartial stylesheets [_ _ old-sheets]
  (vec (conj (vec (butlast old-sheets))
             (link-to-asset "incise.css.stefon"))))

(defpartial head [_ _ old-head]
  (vec (conj (vec (butlast (first old-head)))
             [:link {:rel "icon"
                     :type "image/png"
                     :href (to-uri "/assets/images/favicon.png")}])))

(deflayout incise []
  (clear-hooks #'base-layout/head) ; Necessary to remove old favicon
  (repartial base-layout/head head)
  (repartial vm-layout/stylesheets stylesheets)
  (repartial base-layout/header header)
  (use-layout vm-layout/vm))

(register :incise-layout incise)
