(ns incise.layouts.page
  (:require [incise.layouts.core :refer [register]]
            (hiccup [page :refer :all]
                    [core :refer :all])))

(defn page [{:keys [site-title]} content]
  (let [{:keys [title date tags category]} (meta content)]
    (html5
      [:head [:title (str site-title (when title (str " - " title)))]]
      [:body content])))

(register :page page)
(register :post page)
