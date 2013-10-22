(ns incise.once-spec
  (:require [incise.once :refer [once]]
            [stefon.util :refer [temp-dir]]
            [clojure.java.io :refer [file]]
            [speclj.core :refer :all]))

(def spec-temp-dir (partial temp-dir "incise-once-spec"))

(describe :once
  (with-all out-dir (spec-temp-dir))
  (with-all out-dir-path (.getCanonicalPath @out-dir))
  (with once-result (once :in-dir "resources/spec"
                          :precompiles []
                          :out-dir @out-dir-path))
  (it "returns files parsed"
    (doseq [filename-title ["another-forgotten-binding-pry" "my-short-page"]]
      (should-contain (file @out-dir-path
                            "2013" "8" "12" filename-title "index.html")
                      @once-result))))

(run-specs)
