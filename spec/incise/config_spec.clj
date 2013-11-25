(ns incise.config-spec
  (:require [incise.config :as conf]
            [speclj.core :refer :all]))

(defn should-contain-bad-manner
  [message thing]
  (should-contain message (conf/bad-config-manners thing)))

(describe "config validation"
  (with base-config (conf/get))
  (it "the default config is valid" (conf/avow!))
  (it "requires an in-dir"
    (should-contain-bad-manner "must have an input directory (in-dir)"
                               (dissoc @base-config :in-dir)))
  (it "requires an out-dir"
    (should-contain-bad-manner "must have an output directory (out-dir)"
                               (dissoc @base-config :out-dir)))
  (context "uri-root"
    (it "is a good when it is a string without a leading or trailing slash"
      (conf/avow-config! (assoc @base-config :uri-root "jsj")))
    (it "is a good when it is nil"
      (conf/avow-config! (assoc @base-config :uri-root nil)))
    (it "is bad when it is not a string"
      (should-contain-bad-manner "uri-root must be a string"
                                 (assoc @base-config :uri-root 1)))
    (it "is bad when it has a leading or trailing slash"
      (doseq [bad-path ["/hss" "jsjs/" "/LOL/"]]
        (should-contain-bad-manner
          "uri-root must not start or end with a \"/\""
          (assoc @base-config :uri-root bad-path))))))

(run-specs)
