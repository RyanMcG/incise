(ns incise.layouts.impl.page
  (:require [incise.layouts.core :refer [register]]
            [incise.parsers.core]
            [dieter.core :refer [link-to-asset]]
            (hiccup [page :refer :all]
                    [core :refer :all])))

(defn page
  "The default page/post layout."
  [{:keys [site-title]}
   ^incise.parsers.core.Parse {:keys [title content]}]
  (html5
    [:head
     [:title (str site-title (when title (str " - " title)))]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     (include-css (link-to-asset "stylesheets/app.css.dieter"))]
    [:body#page content
     (include-js "http://code.jquery.com/jquery-2.0.3.min.js"
                 (link-to-asset "javascripts/app.js.dieter"))]))

(register :page page)
(register :post page)
