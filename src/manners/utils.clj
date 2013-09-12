(ns manners.utils)

(defmacro defalias
  [name value]
  `(let [doc-str# (str "This is an alias of " (var ~value)\.)]
     (def ~name ~value)
     (alter-meta! (var ~name) assoc :doc doc-str#)))
