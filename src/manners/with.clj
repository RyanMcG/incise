(ns manners.with
  (:require [manners.core :as core]))

(declare ^:private ^:dynamic *validations*)

(doseq [fault-sym ['faults 'faulty?
                   'faultless? 'faultless! 'manners]]
  (let [core-sym (symbol (str "core/" fault-sym))]
    (eval `(defn ~fault-sym [value#]
             (~core-sym *validations* value#)))))

(defmacro with-validations
  "Start a scope with the given validations as the base."
  [validations & body]
  `(binding [*validations* ~validations]
     ~@body))
