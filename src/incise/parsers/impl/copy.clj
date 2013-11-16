(ns incise.parsers.impl.copy
  (:require [incise.parsers.core :as pc]
            [incise.utils :refer [remove-prefix-from-path]]
            [clojure.java.io :refer [file copy]]
            [incise.config :as conf]))

(defn parse
  "An incise parser which simply copies a file based on its relative path to the
  input directory."
  [input-file]
  (let [{:keys [in-dir out-dir]} (conf/get)
        input-filename (remove-prefix-from-path in-dir input-file)
        output-file (file out-dir input-filename)]
    (delay
      (.mkdirs (.getParentFile output-file))
      (copy input-file output-file)
      [output-file])))

(pc/register [:copy :cname] parse)
