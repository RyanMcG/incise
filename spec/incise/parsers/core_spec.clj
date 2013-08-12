(ns incise.parsers.core-spec
  (:require [speclj.core :refer :all]
            [incise.parsers.core :refer :all])
  (:import [java.io File]))

(describe "extension"
  (with coffee-file (File. "boom/my/coolio.js.coffee"))
  (it "gets the extension of a file"
    (should= (extension @coffee-file) "coffee"))

  (with md-file (File. "my.articles/wow.pants.MARKDOWN"))
  (it "enforces a lower-case extension"
    (should= (extension @md-file) "markdown")))

(describe "Parse record"
  (with parse-rec (map->Parse {:title "Hey"
                               :content "<p>some content</p>"}))
  (it "has a title" (should (string? (:title @parse-rec))))
  (it "have content" (should (string? (:content @parse-rec)))))

(run-specs)
