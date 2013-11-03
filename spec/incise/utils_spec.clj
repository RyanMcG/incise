(ns incise.utils-spec
  (:require [incise.utils :refer :all]
            [stefon.util :refer [temp-dir]]
            [clojure.java.io :refer [file]]
            [speclj.core :refer :all]))

(describe "remove-prefix-from-path"
  (with dir (file "/hey/there/"))
  (with afile (file "/hey/there/pants/party"))
  (it "returns a relative path from the given directory"
    (should= "pants/party" (remove-prefix-from-path @dir @afile))))

(run-specs)
