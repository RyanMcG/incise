(ns incise.layouts.impl.ryanmcg
  (:require (incise.layouts [html :refer [repartial use-layout
                                          deflayout defpartial]]
                            [core :refer [register]])
            [robert.hooke :refer [add-hook]]
            [incise.layouts.impl.page :refer [page]]
            (hiccup [def :refer :all]
                    [element :refer :all])))

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

(defpartial footer
  "A footer parital with a Creative Commons license attached."
  [{:keys [contacts author]} _]
  [:hr]
  [:footer
   [:div.row
    [:div#cc.col-md-8
     [:div.row
      (link-to {:class "col-md-2" :rel "license"}
               "http://creativecommons.org/licenses/by-sa/3.0/deed.en_US"
               (image "http://i.creativecommons.org/l/by-sa/3.0/88x31.png"
                      "Creative Commons License"))
      [:p.col-md10-
       "Content on this website is licensed under a "
       (link-to {:rel "license"}
                "http://creativecommons.org/licenses/by-sa/3.0/deed.en_US"
                "Creative Commons Attribution-ShareAlike 3.0 Unported License")
       " by " author "."]]]
    [:div#contacts.col-md-4 (map contact-spec contacts)]]
   [:p
    "This website is "
    (link-to "https://github.com/RyanMcG/incise" "incise") "d."]])

(deflayout ryanmcg
  (repartial incise.layouts.impl.page/javascripts
             #(cons "http://code.jquery.com/jquery-2.0.3.min.js" %))
  (repartial incise.layouts.impl.page/footer footer)
  (use-layout page))

(register [:ryanmcg] ryanmcg)
