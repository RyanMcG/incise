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
            [robert.hooke :refer [add-hook]]
            [clojure.string :as s]
            [incise.server :refer [wrap-log-exceptions serve stop-server
                                   defserver]])
  (:import [java.io File]))

(defn -main
  "Start the development server and watcher."
  [& args]
  (apply serve args))

(defn once [& {:as config}]
  (load-all)
  (conf/merge config)
  (let [out-dir (conf/get :out-dir)
        stefon-pre-opts {:cache-mode :production
                         :compress true
                         :serving-root out-dir
                         :precompiles (conf/get :precompiles)}]
    (info "Clearing out" (str \" out-dir \"))
    (delete-recursively (file out-dir))
    (with-options stefon-pre-opts
      (info "Precompiling assets...")
      (info (with-out-str (dc/precompile stefon-pre-opts)))
      (info "Done.")
      (->> (conf/get :in-dir)
           (file)
           (file-seq)
           (map (wrap-log-exceptions parse))
           (dorun)))))

(defn restart
  "Define a server and set up a watcher AT THE SAME TIME!"
  [& args]
  (stop-server)
  (apply defserver args))
