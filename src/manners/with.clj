(ns manners.with
  (:require [manners.modern :as modern]
            [manners.victorian :as victorian]))

(declare ^:private ^:dynamic *etiquette*)


(doseq [[dialect syms] {'victorian ['bad-manners 'rude? 'proper? 'avow! 'coach]
                        'modern ['errors 'invalid? 'valid?
                                 'validate! 'validator]}]
  (doseq [sym syms]
    (let [func-sym (symbol (str dialect \/ sym))]
      (eval `(defn ~sym [value#]
               (~func-sym *etiquette* value#))))))

(defmacro with-etiquette
  "Start a scope with the given etiquettes as the base."
  [etiquette & body]
  `(binding [*etiquette* ~etiquette]
     ~@body))
