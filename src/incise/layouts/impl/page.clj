(ns incise.layouts.impl.page
  (:require (incise.layouts [core :refer [register]]
                            [html :refer [repartial
                                          use-layout
                                          deflayout
                                          defpartial]])
            incise.layouts.impl.base
            [hiccup.element :refer [link-to]]))

(defpartial header
  "A very basic header partial."
  [{:keys [site-title]} _]
  [:header
   [:h1#site-title (link-to "/" site-title)]])

(deflayout page
  "The default page/post layout."
  []
  (repartial incise.layouts.impl.base/header header)
  (use-layout incise.layouts.impl.base/base))

(register [:page :post] page)
