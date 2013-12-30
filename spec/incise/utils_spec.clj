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

(describe "slot-by"
  (with hash-1 {:tags [:a :b :c] :value 1})
  (with hash-2 {:tags [:b] :value 2})
  (with hash-3 {:tags [:c :d] :value 3})
  (with coll [@hash-1 @hash-2 @hash-3])
  (it "creates a persistent collection slotted by tags"
    (should= {:a [@hash-1]
              :b [@hash-1 @hash-2]
              :c [@hash-1 @hash-3]
              :d [@hash-3]}
             (slot-by :tags @coll)))
  (it "converts non sequential keys-fn return values to vectors"
    (should= {1 [@hash-1]
              2 [@hash-2]
              3 [@hash-3]}
             (slot-by :value @coll))))

(describe "normalize-uri"
  (context "the given uri terminates with a '/'"
    (with uri "/hey/")
    (it "prepends index.html"
      (should= (str @uri "index.html") (normalize-uri @uri))))
  (context "the given uri does not terminate with a '/'"
    (with uri "/hey.html")
    (it "does nothing"
      (should= @uri (normalize-uri @uri)))))

(run-specs)
