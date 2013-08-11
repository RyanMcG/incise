(ns incise.layouts.core)

(def layouts (atom {}))

(defn register
  "Register a layout function to a shortname"
  [short-name layout-fn]
  (swap! layouts
         assoc (str short-name) layout-fn))
