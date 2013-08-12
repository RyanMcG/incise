(ns incise.parsers.markdown-spec
  (:require [speclj.core :refer :all]
            [incise.parsers.markdown :refer :all])
  (:import [java.io File]))

(describe "parsing"
  (it "does something"
    (should (parse (File. "hey")))))

(run-specs)
