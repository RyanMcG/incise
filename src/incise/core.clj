(ns incise.core
  (:require (incise [load :refer [load-parsers-and-layouts]]
                    [config :as conf]
                    [once :refer [once]]
                    [utils :refer [delete-recursively]])
            [incise.deploy.core :refer [deploy]]
            [taoensso.timbre :refer [warn]]
            [clojure.string :as s]
            [clojure.tools.cli :refer [cli]]
            [incise.server :refer [wrap-log-exceptions serve]]))

(def ^:private valid-methods #{"serve" "once" "deploy"})
(defn- parse-method [method]
  (if (contains? valid-methods method)
    (keyword method)
    (do
      (when-not (empty? method)
        (warn (str \" method
                   "\" is not a valid method (must be in "
                   (s/join ", " valid-methods) "). Defaulting to serve.")))
      :serve)))

(defn- with-args*
  "A helper function to with-args macro which does all the work.

  1.  Load in the config
  2.  Parse arguments
  3.  Merge into config
  4.  Handle help or continue"
  [args body-fn]
  (conf/load)
  (let [[options cli-args banner]
        (cli args
             "A tool for incising."
             ["-h" "--help" "Print this help." :default false :flag true]
             ["-m" "--method" "serve, once, or deploy"
              :default :serve :parse-fn parse-method]
             ["-i" "--in-dir" "The directory to get source from"]
             ["-o" "--out-dir" "The directory to put content into"])]
    (conf/merge options)
    (if (:help options)
      (do (println banner)
          (System/exit 0))
      (body-fn options cli-args))))

(defmacro with-args
  "Take arguments parsing them using cli and handle help accordingly."
  [args & body]
  `(with-args* ~args (fn [~'options ~'cli-args] ~@body)))

(defn wrap-pre [func pre-func & more]
  (fn [& args]
    (apply pre-func more)
    (apply func args)))

(defn wrap-post [func post-func & more]
  (fn [& args]
    (let [return-value (apply func args)]
      (apply post-func more)
      return-value)))

(defn wrap-serve
  [main-func]
  (-> main-func
      (wrap-pre conf/avow!)
      (wrap-pre conf/load)
      (wrap-log-exceptions :bubble false)))

(defn wrap-main
  [main-func]
  (-> main-func
      (wrap-serve)
      (wrap-post #(System/exit 0))))

(defn -main
  "Based on the given args either deploy, compile or start the development
  server."
  [& args]
  (with-args args
    ((case (:method options)
       :deploy (wrap-main deploy)
       :once (wrap-main once)
       :serve (wrap-serve serve)))))
