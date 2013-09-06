(ns incise.core
  (:require (incise [load :refer [load-all]]
                    [config :as conf]
                    [utils :refer [delete-recursively]])
            [incise.parsers.core :refer [parse]]
            [taoensso.timbre :refer [info]]
            (dieter [settings :refer [with-options]]
                    [core :as dc]
                    [path :refer [adrf->uri]]
                    [cache :as dcache])
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
        dieter-pre-opts {:cache-mode :production
                         :compress false
                         :engine :v8
                         :precompiles (conf/get :precompiles)
                         :cache-root out-dir}]
    (info "Clearing out" (str \" out-dir \"))
    (delete-recursively (file out-dir))
    (add-hook #'dieter.cache/cached-file-path
              (fn [f & more]
                (let [[adrf] more
                      cached-path (apply f more)]
                  (dcache/add-cached-uri (adrf->uri adrf)
                                         (.substring cached-path
                                                     (count out-dir)))
                  cached-path)))
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
