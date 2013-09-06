(ns incise.layouts.impl.page
  (:require (incise.layouts [core :refer [register]]
                            [html :refer [deflayout defpartial]])
            [stefon.core :refer [link-to-asset]]
            (hiccup [def :refer :all]
                    [page :refer :all]
                    [element :refer :all]
                    [core :refer :all])))

(defpartial header
  "A very basic header partial."
  [{:keys [site-title]} _]
  [:header
   [:h1#site-title (link-to "/" site-title)]])

(defpartial footer
  "A very basic footer crediting this project."
  [_ {:keys [contacts author]}]
  [:footer
   [:p
    "This website is "
    (link-to "https://github.com/RyanMcG/incise" "incise") "d."]])

(defn stylesheets []
  [(link-to-asset "stylesheets/app.css.stefon")])

(defn javascripts []
   [(link-to-asset "javascripts/app.js.stefon")])

(deflayout page
  "The default page/post layout."
  [:head
   [:title (str site-title (when title (str " - " title)))]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0"}]
   (apply include-css (stylesheets))]
  [:body#page
   [:div.container
    (header)
    [:div#content content]
    (footer)]
   (apply include-js (javascripts))])

(register [:post :page] page)
