(ns manners.core
  (:require [clojure.string :as s]))

(defn- wrap-try
  "Create a function which has the same behaviour as func but catches all
   exceptions returning nil if one is received."
  [func]
  (fn [& more]
    (try (apply func more) (catch Exception _))))

(defn- unmemoized-manners
  "Return a memoized function which takes a value to run the given validations
   on."
  [validations]
  {:pre [(sequential? validations)]}
  (memoize (fn [value]
             (for [[predicate & message] validations
                   :when ((-> predicate wrap-try complement) value)]
               (apply str message)))))

(def manners
  "Create a function from a sequence of validators that returns a sequence of
   faults."
  (memoize unmemoized-manners))

(defn faults
  "Return all faults found with the given validations on the given value."
  [validations value]
  ((manners validations) value))

(defn faultless?
  "A predicate to determine if the given value has any faults according to the
   validations."
  [validations value]
  (empty? (faults validations value)))

(def faulty? (complement faultless?))

(defn falter
  "Throw an AssertionError when there are faults."
  ([prefix-sym faults]
   (let [full-prefix (when prefix-sym (str "Invalid " prefix-sym ": "))]
     (when-not (empty? faults)
       (throw (AssertionError. (str full-prefix (s/join ", " faults)))))))
  ([faults] (falter nil faults)))

(defn faultless!
  "Throw an AssertionError if there are any faults found on the given value with
   the given validations."
  ([prefix validations value] (falter prefix (faults validations value)))
  ([validations value] (faultless! nil validations value)))

(defmacro demannerss
  [obj-sym & validations]
  (let [faults-sym (symbol (str obj-sym "-faults"))
        faultless?-sym (symbol (str "faultless-" obj-sym \?))
        faulty?-sym (symbol (str "faulty-" obj-sym \?))
        faultless!-sym (symbol (str "faultless-" obj-sym \!))]
    `(do
       (def ~faults-sym
         (partial faults [~@validations]))
       (def ~faultless?-sym
         (partial faultless? [~@validations]))
       (def ~faulty?-sym
         (partial faulty? [~@validations]))
       (defn ~faultless!-sym [value#]
         (faultless! (quote ~obj-sym) [~@validations] value#)))))
