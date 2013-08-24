(ns incise.parsers.impl.markdown-spec
  (:require [speclj.core :refer :all]
            [clojure.java.io :refer [file resource]]
            [incise.core] ; Ensure that layouts have been loaded
            [incise.parsers.core :as pc]
            [incise.parsers.impl.markdown :refer :all])
  (:import [java.io File]))

(describe "parsing"
  (with markdown-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (it "does something"
    (should= "" (pc/Parse->string (parse @markdown-file)))))

(run-specs)
