(ns incise.config
  (:refer-clojure :exclude [assoc get]))

(def config (atom {}))

(defn assoc [& args]
  (apply swap! config assoc args))

(defn get [& args]
  (if (empty? args)
    @config
    (apply @config args)))
