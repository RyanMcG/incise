(ns incise.layouts.impl.incise
  (:require (incise.layouts [utils :refer [repartial use-layout deflayout]]
                            [core :refer [register]])
            (incise.layouts.impl [page :as page-layout]
                                 [base :as base-layout])
            [hiccup.util :refer [to-uri]]))

(deflayout incise []
  (repartial base-layout/head
             #(conj % [:link {:rel "icon"
                              :type "image/png"
                              :href (to-uri "/assets/images/favicon.png")}]))
  (use-layout page-layout/page))

(register :incise incise)
