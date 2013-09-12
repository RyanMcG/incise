(ns incise.once
  (:require (incise [load :refer [load-parsers-and-layouts]]
                    [config :as conf]
                    [utils :refer [delete-recursively]])
            [clojure.java.io :refer [file]]
            [incise.parsers.core :refer [parse]]
            [taoensso.timbre :refer [info]]
            (stefon [settings :refer [with-options]]
                    [core :refer [precompile]])))

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
      (info (with-out-str (precompile)))
      (info "Done.")
      (load-parsers-and-layouts)
      (->> (conf/get :in-dir)
           (file)
           (file-seq)
           (map parse)
           (dorun)))))
