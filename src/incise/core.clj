(ns incise.core
  (:require (incise [config :as conf]
                    [once :refer [once]]
                    [utils :refer [getenv]]
                    [server :refer [wrap-log-exceptions start]])
            [incise.deploy.core :refer [deploy]]
            [taoensso.timbre :refer [warn]]
            [clojure.string :as s]
            [clojure.tools.cli :refer [parse-opts]]))

(def ^:private valid-methods #{:serve :once :deploy})

(def cli-options
  [["-h" "--help" "Print this help."]
   ["-m" "--method METHOD" "serve, once, or deploy"
    :default :serve
    :parse-fn #(keyword %)
    :validate [#(contains? valid-methods %)
               "method must be either serve, once or deploy"]]
   ["-c" "--config CONFIG_FILE"
    "The path to an edn file acting as configuration for incise"]
   ["-p" "--port PORT" "The port number to run the development server on."
    :default (getenv "INCISE_PORT" 5000)
    :parse-fn #(Integer. %)
    :validate [#(< 0 % 0x10000)
               "Must be a number between 0 and 65536"]]
   ["-t" "--thread-count THREAD_COUNT"
    "The number of threads for the development server to use."
    :default (getenv "INCISE_THREAD_COUNT" 4)
    :parse-fn #(Integer. %)]
   ["-g" "--ignore-publish"
    "Ignore the publish config for content (i.e. parse regardless)."]
   ["-i" "--in-dir INPUT_DIRECTORY" "The directory to get source from"]
   ["-o" "--out-dir OUTPUT_DIRECTORY" "The directory to put content into"]
   ["-u" "--uri-root URI_ROOT"
    "The path relative to the domain root where the generated site will be hosted."]])

(defn exit [code & messages]
  (dorun (map println messages))
  (System/exit code))

(defn with-args*
  "A helper function to with-args macro which does all the work.

  1.  Parse arguments
  2.  Load in the config
  3.  Merge some options into config
  4.  Handle help or call body-fn with options and cli arguments"
  [args body-fn]
  (let [{:keys [options arguments
                summary errors]} (parse-opts args cli-options)]
    (when (seq errors)
      (apply exit
             (count errors)
             (conj errors "" summary)))
    (when (:help options)
      (exit 0
            "A tool for incising."
           ""
            summary))
    (conf/load (options :config))
    (conf/merge! (dissoc options :config :help))
    (body-fn options arguments)))

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
       :serve (wrap-serve start)))))
