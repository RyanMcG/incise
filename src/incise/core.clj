(ns incise.core
  (:require (incise [load :refer [load-all]]
                    [config :as conf]
                    [utils :refer [delete-recursively]])
            [incise.parsers.core :refer [parse]]
            [taoensso.timbre :refer [info]]
            (dieter [settings :refer [with-options]]
                    [core :as dc])
            [clojure.java.io :refer [file]]
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
  (delete-recursively (file (conf/get :out-dir)))
  (let [dieter-pre-opts {:cache-mode :production
                         :compress false
                         :engine :v8
                         :precompiles (conf/get :precompiles)
                         :cache-root (conf/get :out-dir)}]
    (with-options dieter-pre-opts
      (info "Precompiling assets...")
      (info (with-out-str (dc/precompile dieter-pre-opts)))
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
