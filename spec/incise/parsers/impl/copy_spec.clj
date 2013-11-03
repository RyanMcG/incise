(ns incise.parsers.impl.copy-spec
  (:require [incise.parsers.impl.copy :refer :all]
            [incise.config :as conf]
            [stefon.util :refer [temp-dir]]
            [clojure.java.io :refer [file resource]]
            [speclj.core :refer :all]))

(def spec-temp-dir (partial temp-dir "incise-copy-spec"))

(defn in-directory? [directory file-in-dir]
  ""
  (let [dir-path (.getCanonicalPath directory)
        file-path (.getCanonicalPath file-in-dir)]
    (= dir-path (subs file-path 0 (count dir-path)))))

(describe "relative-path-from"
  (with dir (file "/hey/there/"))
  (with afile (file "/hey/there/pants/party"))
  (it "returns a relative path from the given directory"
    (should= "pants/party" (relative-path-from @dir @afile))))

(describe "parse"
  (with copyme (file (resource "spec/COPYME")))
  (with out-dir (spec-temp-dir))
  (before (conf/merge {:out-dir @out-dir
                       :in-dir (.getParent (.getParentFile @copyme))}))
  (with out-file (parse @copyme))
  (it "actually copies the file"
    (should (.exists @out-file)))
  (it "preserves file name and directory structure"
    (should= (file @out-dir "spec/COPYME") @out-file))
  (it "preserves the content of the file"
    (should= (slurp @out-file) "I am to be copied in a spec.\n"))
  (it "copies the file into the correct directory"
    (should (in-directory? @out-dir @out-file))))

(run-specs)
