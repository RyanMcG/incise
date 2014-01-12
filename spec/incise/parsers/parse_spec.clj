(ns incise.parsers.parse-spec
  (:require [speclj.core :refer :all]
            [incise.config :as conf]
            [incise.parsers.parse :refer :all]
            [clojure.java.io :refer [file resource]]))


(describe "parses"
  (it "is an atom" (should (instance? clojure.lang.Atom parses)))
  (it "is a map when derferenced" (should (map? @parses))))

(defmacro with-clean-parses [& body]
  `(with-redefs [parses @parses-stub]
     ~@body))

(describe "record-parse"
  (with parses-stub (atom {}))
  (with a-parse (map->Parse {}))
  (with path "my/cool/path.md")
  (around [it] (with-clean-parses (it)))
  (it "records the specified Parse at the specified path in parses atom"
    (record-parse @path @a-parse)
    (should= {@path @a-parse} @parses)))

(describe "dissoc-parses"
  (with path "my/cool/path.md")
  (with parses-stub (atom {@path (map->Parse {})}))
  (around [it] (with-clean-parses (it)))
  (it "removes the specified paths from the parses atom"
    (dissoc-parses [@path])
    (should= {} @parses)))

(defmacro should-always-publish []
  `(do
     (should (publish-parse? @publish-parse))
     (should (publish-parse? @nil-publish-parse))
     (should (publish-parse? @no-publish-parse))))

(describe "publish-parse?"
  (with ignore-publish false)
  (with serving? false)
  (with publish-parse (map->Parse {:publish true}))
  (with nil-publish-parse (map->Parse {}))
  (with no-publish-parse (map->Parse {:publish false}))
  (around [it]
    (with-redefs [conf/get (fn [& _#] @ignore-publish)
                  conf/serving? (fn [] @serving?)]
      (it)))
  (it "pays attention to parse config when nothing overrides"
    (should (publish-parse? @publish-parse))
    (should (publish-parse? @nil-publish-parse))
    (should-not (publish-parse? @no-publish-parse)))
  (context "when serving"
    (with serving? true)
    (it "always publishes" (should-always-publish)))
  (context "when ignoring publish"
    (with ignore-publish true)
    (it "always publishes" (should-always-publish))))

(run-specs)
