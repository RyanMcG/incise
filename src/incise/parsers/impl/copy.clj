(ns incise.parsers.impl.copy
  (:require [incise.parsers.core :as pc]
            [clojure.java.io :refer [file copy]]
            [incise.config :as conf]))

(defn relative-path-from [directory afile]
  (subs (.getCanonicalPath afile) (inc (count (.getCanonicalPath directory)))))

(defn parse [input-file]
  (let [{:keys [in-dir out-dir]} (conf/get)
        input-filename (relative-path-from (file in-dir) input-file)
        output-file (file out-dir input-filename)]
    (.mkdirs (.getParentFile output-file))
    (copy input-file output-file)
    output-file))

(pc/register [:copy] parse)
