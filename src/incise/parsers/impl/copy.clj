(ns incise.parsers.impl.copy
  (:require [incise.parsers.core :as pc]
            [incise.utils :refer [remove-prefix-from-path]]
            [clojure.java.io :refer [file copy]]
            [incise.config :as conf]))

(defn parse [input-file]
  (let [{:keys [in-dir out-dir]} (conf/get)
        input-filename (remove-prefix-from-path in-dir input-file)
        output-file (file out-dir input-filename)]
    (.mkdirs (.getParentFile output-file))
    (copy input-file output-file)
    output-file))

(pc/register [:copy] parse)
