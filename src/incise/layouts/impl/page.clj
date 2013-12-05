(ns incise.layouts.impl.page
  (:require (incise.layouts [core :refer [register]]
                            [html :refer [repartial
                                          use-layout
                                          deflayout
                                          defpartial]])
            [incise.layouts.impl.base :as base]
            (hiccup [element :refer [link-to]]
                    [core :refer [html]])))

(defpartial header
  "A very basic header partial."
  [{:keys [site-title]} _]
  (html
    [:header
     [:h1#site-title (link-to "/" site-title)]]))

(deflayout page
  "The default page/post layout."
  []
  (repartial base/header header)
  (use-layout base/base))

(register [:page :post] page)
