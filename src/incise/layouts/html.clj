(ns incise.layouts.html
  (:require [incise.parsers.parse]
            [robert.hooke :refer [with-scope add-hook]]))

(declare ^:dynamic *site-options*)
(declare ^:dynamic *parse*)

(defmacro deflayout
  "This is a helper macro for defining html layout functions. An html layout
  function at its core is just a function which takes two arguments:

    site-options - Global options for the site from incise.edn
    parse        - An instance of Parse containing keys such as content and
                   title

  This macro makes it just a little bit easier to define such functions by
  taking care of some boiler plate stuff like using the robert.hooke with-scope
  macro and binind the functions arguments to *site-options* and *parse* so
  partials can easily access them."
  [sym-name doc-string? destructuring & body]
  (let [[doc-string destructuring]
        (if (string? doc-string?)
          [doc-string? destructuring]
          ["" doc-string?])]
    `(defn ~sym-name
       ~doc-string
       [site-options# ^incise.parsers.parse.Parse parse#]
       (binding [*site-options* site-options#
                 *parse* parse#]
         (let [~destructuring [*site-options* *parse*]]
           (with-scope ~@body))))))

(defmacro defpartial
  "Defines a 'partial' which is baically some hiccup markup. An arguments vector
  is required and can be used to destructure the dynamic vars set by a layout."
  [sym-name doc-string? destructuring & body]
  (let [[doc-string destructuring body]
        (if (string? doc-string?)
          [doc-string? destructuring body]
          ["" doc-string? (conj body destructuring)])]
    `(defn ~sym-name
       ~doc-string
       [& args#]
       (let [~destructuring [*site-options* *parse* args#]]
         ~@body))))

(defmacro repartial
  "Replace the given var in a layout with a new function which will be called in
   its place and passed the result of the fn it is replacing. Returns an empty
   string so it can be used in layouts without issues."
  [to-be-replaced replacement]
  `(do
     (add-hook #'~to-be-replaced
               (fn [f# & args#] (~replacement (apply f# args#))))
     ""))

(defn eval-with-context [code]
  (binding [*ns* (create-ns `user#)]
    (require '[clojure.core :refer :all])
    (require '[incise.layouts.html :refer [*site-options* *parse*]])
    (eval `(do
             (def ~'parses (vals @incise.parsers.parse/parses))
             (def ~'tags (incise.utils/slot-by :tags ~'parses))
             ~@code))))

(defn use-layout
  "Use the given layout function by calling it with *site-options* and *parse*."
  [layout-fn] (layout-fn *site-options* *parse*))
