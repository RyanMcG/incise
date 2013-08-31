(ns incise.layouts.impl.page
  (:require [incise.layouts.core :refer [register]]
            [incise.parsers.core]
            [dieter.core :refer [link-to-asset]]
            (hiccup [def :refer :all]
                    [page :refer :all]
                    [element :refer :all]
                    [core :refer :all])))

(defhtml header
  [{:keys [site-title] :as site-options} parse]
  [:header
   [:h1#site-title (link-to "/" site-title)]])

(defmulti contact (fn [x & _] x))
(defmethod contact :default
  [end-point handle]
  (link-to (str "https://" (name end-point) ".com/" handle)
           (str \@ handle)))
(defmethod contact :email
  [_ email]
  (mail-to email))

(defhtml contact-spec [[end-point handle]]
  [:div.contact
   [:span.end-point end-point]
   (contact end-point handle)])

(defhtml footer
  [{:keys [contacts author] :as site-options} parse]
  [:hr]
  [:footer
   [:div.row
    [:div#cc.col-md-8
     (link-to {:rel "license"} "http://creativecommons.org/licenses/by-sa/3.0/deed.en_US"
              (image "http://i.creativecommons.org/l/by-sa/3.0/88x31.png" "Creative Commons License"))
     [:p "Content on this website is licensed under a "
      (link-to {:rel "license"} "http://creativecommons.org/licenses/by-sa/3.0/deed.en_US"
               "Creative Commons Attribution-ShareAlike 3.0 Unported License")
      " by " author "."]]
    [:div#contacts.col-md-4 (map contact-spec contacts)]]
    [:p "This website is " (link-to "https://github.com/RyanMcG/incise" "incise") "d."]])

(defn page
  "The default page/post layout."
  [{:keys [site-title] :as site-options}
   ^incise.parsers.core.Parse {:keys [title content] :as parse}]
  (html5
    [:head
     [:title (str site-title (when title (str " - " title)))]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     (include-css (link-to-asset "stylesheets/app.css.dieter"))]
    [:body#page
     [:div.container
      (header site-options parse)
      [:div#content content]
      (footer site-options parse)]
     (include-js "http://code.jquery.com/jquery-2.0.3.min.js"
                 (link-to-asset "javascripts/app.js.dieter"))]))

(register :page page)
(register :post page)
