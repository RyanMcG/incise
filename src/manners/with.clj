(ns manners.with
  (:require [manners.victorian :as victorian]))

(declare ^:private ^:dynamic *validations*)

(doseq [fault-sym ['faults 'faulty?
                   'faultless? 'faultless! 'manners]]
  (let [victorian-sym (symbol (str "victorian/" fault-sym))]
    (eval `(defn ~fault-sym [value#]
             (~victorian-sym *validations* value#)))))

(defmacro with-validations
  "Start a scope with the given validations as the base."
  [validations & body]
  `(binding [*validations* ~validations]
     ~@body))
