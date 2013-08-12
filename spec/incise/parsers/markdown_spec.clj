(ns incise.parsers.markdown-spec
  (:require [speclj.core :refer :all]
            [clojure.java.io :refer [file resource]]
            [incise.parsers.markdown :refer :all])
  (:import [java.io File]))

(describe "parsing"
  (with markdown-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (it "does something"
    (should (parse @markdown-file))))

(run-specs)
