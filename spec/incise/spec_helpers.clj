(ns incise.spec-helpers
  "A namespace for defining helpers which are used in multiple spec namespaces."
  (:require incise.config
            [speclj.core :refer [around]]))

(defmacro around-with-custom-config [& {:as custom-config}]
  `(around [it#]
     (with-redefs [incise.config/config
                   (atom (merge @incise.config/config
                                ~custom-config))]
       (it#))))
