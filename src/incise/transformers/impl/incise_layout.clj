(ns incise.transformers.impl.incise-layout
  (:require (incise.transformers [layout :refer [repartial use-layout
                                                 deflayout defpartial]]
                                 [core :refer [register]])
            [stefon.core :refer [link-to-asset]]
            [incise.transformers.impl.vm-layout :as vm-layout]
            [incise.transformers.impl.base-layout :as base-layout]))

(defpartial header []
  [:header
   [:h1 [:img {:alt "inciꞅe"
               :src "https://raw.github.com/RyanMcG/incise/master/website/content/assets/images/logo.png"}]
    [:a {:href "https://travis-ci.org/RyanMcG/incise-core"
         :title "Build Status"}
     [:img {:alt "Build Status"
            :src "https://travis-ci.org/RyanMcG/incise-core.png?branch=master"}]]]
   [:span.tag-line "An extensible static site generator written in Clojure."]])

(defpartial stylesheets [_ _ old-sheets]
  (vec (conj (butlast old-sheets)
             (link-to-asset "incise.css.stefon"))))

(deflayout incise []
  (repartial base-layout/header header)
  (repartial vm-layout/stylesheets stylesheets)
  (use-layout vm-layout/vm))

(register :incise-layout incise)
