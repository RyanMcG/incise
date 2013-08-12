(ns incise.parsers.markdown-spec
  (:require [speclj.core :refer :all]
            [clojure.java.io :refer [file resource]]
            [incise.parsers.core :as pc]
            [incise.parsers.markdown :refer :all])
  (:import [java.io File]))

(describe "parsing"
  (with markdown-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (it "does something"
    (should (pc/Parse->html (parse @markdown-file)))))

(run-specs)
