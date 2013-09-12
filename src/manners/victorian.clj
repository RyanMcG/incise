(ns manners.victorian
  (:require [clojure.string :as s]))

(defn- wrap-try
  "Create a function which has the same behaviour as func but catches all
   exceptions returning nil if one is received."
  [func]
  (fn [& more]
    (try (apply func more) (catch Exception _))))

(defn- unmemoized-coach
  "Return a memoized function which takes a value to run the given etiquette
   on."
  [etiquette]
  {:pre [(sequential? etiquette)]}
  (memoize (fn [value]
             (for [[predicate & message] etiquette
                   :when ((-> predicate wrap-try complement) value)]
               (apply str message)))))

(def coach
  "Create a function from a sequence of validators that returns a sequence of
   bad manners."
  (memoize unmemoized-coach))

(defn bad-manners
  "Return all bad manners found with the given etiquette on the given value."
  [etiquette value]
  ((coach etiquette) value))

(defn proper?
  "A predicate to determine if the given value has any bad manners according to the
   validations."
  [etiquette value]
  (empty? (bad-manners etiquette value)))

(def rude? (complement proper?))

(defn falter
  "Throw an AssertionError when there are bad manners."
  ([prefix-sym bad-manners]
   (let [full-prefix (when prefix-sym (str "Invalid " prefix-sym ": "))]
     (when-not (empty? bad-manners)
       (throw (AssertionError. (str full-prefix (s/join ", " bad-manners)))))))
  ([bad-manners] (falter nil bad-manners)))

(defn avow!
  "Throw an AssertionError if there are any bad manners found on the given value with
   the given validations."
  ([prefix etiquette value] (falter prefix (bad-manners etiquette value)))
  ([etiquette value] (avow! nil etiquette value)))

(defmacro defmanners
  [obj-sym & etiquette]
  (let [bad-manners-sym (symbol (str "bad-" obj-sym "-manners"))
        proper?-sym (symbol (str "proper-" obj-sym \?))
        rude?-sym (symbol (str "rude-" obj-sym \?))
        avow!-sym (symbol (str "avow-" obj-sym \!))]
    `(do
       (def ~bad-manners-sym
         (partial bad-manners [~@etiquette]))
       (def ~proper?-sym
         (partial proper? [~@etiquette]))
       (def ~rude?-sym
         (partial rude? [~@etiquette]))
       (defn ~avow!-sym [value#]
         (avow! (quote ~obj-sym) [~@etiquette] value#)))))
