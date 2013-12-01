(ns incise.core
  (:require (incise [config :as conf]
                    [once :refer [once]])
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
      (when (seq method)
        (warn (str \" method
                   "\" is not a valid method (must be in "
                   (s/join ", " valid-methods) "). Defaulting to serve.")))
      :serve)))

(defn with-args*
  "A helper function to with-args macro which does all the work.

  1.  Parse arguments
  2.  Load in the config
  3.  Merge some options into config
  4.  Handle help or call body-fn with options and cli arguments"
  [args body-fn]
  (let [uri-root-desc (str "The path relative to the domain root where the "
                           "generated site will be hosted.")
        [options cli-args banner]
        (cli args
             "A tool for incising."
             ["-h" "--help" "Print this help." :default false :flag true]
             ["-m" "--method" "serve, once, or deploy"
              :default :serve :parse-fn parse-method]
             ["-c" "--config" (str "The path to an edn file acting as "
                                   "configuration for incise")]
             ["-i" "--in-dir" "The directory to get source from"]
             ["-o" "--out-dir" "The directory to put content into"]
             ["-u" "--uri-root" uri-root-desc])]
    (conf/load (options :config))
    (conf/merge (dissoc options :config :help))
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
