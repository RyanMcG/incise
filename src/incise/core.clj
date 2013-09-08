(ns incise.core
  "A tool for incising."
  (:require (incise [load :refer [load-parsers-and-layouts]]
                    [config :as conf]
                    [utils :refer [delete-recursively]])
            [incise.parsers.core :refer [parse]]
            [taoensso.timbre :refer [info]]
            (stefon [settings :refer [with-options]]
                    [core :as dc])
            [clojure.java.io :refer [file]]
            [clojure.tools.cli :refer [cli]]
            [robert.hooke :refer [add-hook]]
            [clojure.string :as s]
            [incise.server :refer [wrap-log-exceptions serve stop-server
                                   defserver]])
  (:import [java.io File]))

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
             (:doc (meta (the-ns 'incise.core)))
             ["-h" "--help" "Print this help." :default false :flag true]
             ["-1" "--once" "Instead of running in server mode run once."
              :default false :flag true]
             ["-i" "--in-dir" "The directory to get source from"]
             ["-o" "--out-dir" "The directory to put content into"])]
    (conf/merge options)
    (if (:help options)
      (do (println banner)
          (System/exit 0))
      (body-fn cli-args))))

(defmacro with-args
  "Take arguments parsing them using cli and handle help accordingly."
  [args & body]
  `(with-args* ~args (fn [~'args] ~@body)))

(defn once
  "Incise just once."
  [& [load-config]]
  (when-not (false? load-config) (conf/load))
  (let [out-dir (conf/get :out-dir)
        stefon-pre-opts {:mode :production
                         :serving-root out-dir
                         :precompiles (conf/get :precompiles)}]
    (info "Clearing out" (str \" out-dir \"))
    (delete-recursively (file out-dir))
    (with-options stefon-pre-opts
      (info "Precompiling assets...")
      (info (with-out-str (dc/precompile)))
      (info "Done.")
      (load-parsers-and-layouts)
      (->> (conf/get :in-dir)
           (file)
           (file-seq)
           (map (wrap-log-exceptions parse))
           (dorun)))))

(defn -main
  "Start the development server and watcher."
  [& args]
  (with-args args
    (if (conf/get :once)
      (do (once false)
          (System/exit 0))
      (serve))))

(defn restart
  "Define a server and set up a watcher AT THE SAME TIME!"
  [& args]
  (stop-server)
  (apply defserver args))
