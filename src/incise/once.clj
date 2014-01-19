(ns incise.once
  (:require (incise [load :refer [load-parsers-and-layouts]]
                    [config :as conf]
                    [utils :refer [delete-recursively directory?]])
            [clojure.java.io :refer [file]]
            [incise.parsers.core :refer [parse-all-input-files]]
            [taoensso.timbre :refer [info]]
            (stefon [settings :refer [with-options]]
                    [core :refer [precompile]])))

(defn once
  "Incise just once. This requires that config is already loaded."
  [& {:as config}]
  (conf/merge! config)
  (conf/avow!)
  (let [{:keys [out-dir precompiles stefon]} (conf/get)]
    (info "Clearing out" (str \" out-dir \"))
    (delete-recursively (file out-dir))
    (with-options (merge {:mode :production
                          :serving-root out-dir
                          :precompiles (or precompiles [])} stefon)
      (info "Precompiling assets...")
      (precompile nil)
      (info "Done.")
      (load-parsers-and-layouts)
      (doall (parse-all-input-files)))))
