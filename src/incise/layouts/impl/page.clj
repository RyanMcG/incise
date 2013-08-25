(ns incise.layouts.impl.page
  (:require [incise.layouts.core :refer [register]]
            [incise.parsers.core]
            [dieter.core :refer [link-to-asset]]
            (hiccup [page :refer :all]
                    [core :refer :all])))

(defn page [{:keys [site-title]}
            ^incise.parsers.core.Parse {:keys [title content]}]
  (html5
    [:head
     [:title (str site-title (when title (str " - " title)))]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     (link-to-asset "stylesheets/app.css.dieter")]
    [:body#page content
     (link-to-asset "javascripts/app.js.dieter")]))

(register :page page)
(register :post page)
