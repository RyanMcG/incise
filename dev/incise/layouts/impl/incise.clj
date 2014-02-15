(ns incise.layouts.impl.incise
  (:require (incise.layouts [utils :refer [repartial use-layout defpartial
                                           deflayout]]
                            [core :refer [register]])
            (incise.layouts.impl [page :as page-layout]
                                 [base :as base-layout])
            [stefon.core :refer [link-to-asset]]
            [hiccup.util :refer [to-uri]]))

(defpartial stylesheets [] [(link-to-asset "stylesheets/app.css.stefon")])
(defpartial javascripts [] [(link-to-asset "javascripts/app.js.stefon")])

(deflayout incise []
  (repartial base-layout/head
             #(conj % [:link {:rel "icon"
                              :type "image/png"
                              :href (to-uri "/assets/images/favicon.png")}]))
  (repartial base-layout/stylesheets stylesheets)
  (repartial base-layout/javascripts javascripts)
  (use-layout page-layout/page))

(register :incise incise)
