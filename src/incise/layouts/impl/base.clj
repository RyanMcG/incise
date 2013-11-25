(ns incise.layouts.impl.base
  (:require (incise.layouts [core :refer [register]]
                            [html :refer [eval-with-context
                                          deflayout
                                          defpartial]])
            [stefon.core :refer [link-to-asset]]
            [incise.config :as conf]
            (hiccup [core :refer :all]
                    [util :refer [with-base-url]]
                    [def :refer :all]
                    [page :refer :all]
                    [element :refer :all]))
  (:import [java.io FileNotFoundException]))

(defpartial header "A blank header" [] nil)

(defpartial content
  "A very basic content partial."
  [_ {:keys [content]}]
  (html
    (condp #(%1 %2) content
      list? (eval-with-context content)
      content)))

(defpartial footer
  "A very basic footer crediting this project."
  [{:keys [contacts author]} _]
  (html
    [:footer
     [:p
      "This website was "
      (link-to "https://github.com/RyanMcG/incise" "incised") \.]]))

(defn- attempt-link-to-asset
  "Return nil if the asset is not found othewise return a url for the asset."
  [asset-name]
  (try
    (link-to-asset asset-name nil)
    (catch FileNotFoundException _ nil)))

(defn stylesheets []
  [(attempt-link-to-asset "stylesheets/app.css.stefon")])

(defn javascripts []
  [(attempt-link-to-asset "javascripts/app.js.stefon")])

(deflayout base
  "The default page/post layout."
  [{:keys [site-title]} {:keys [title]}]
  (with-base-url (when-not (conf/serving?) (str \/ (conf/get :uri-root)))
    (html5
      [:head
       [:title (str site-title (when title (str " - " title)))]
       [:meta {:charset "UTF-8"}]
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
       (apply include-css (remove nil? (stylesheets)))]
      [:body#page
       [:div.container
        (header)
        [:div#content (content)]
        (footer)]
       (apply include-js (remove nil? (javascripts)))])))

(register [:base] base)
