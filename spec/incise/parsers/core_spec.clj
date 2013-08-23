(ns incise.parsers.core-spec
  (:require [speclj.core :refer :all]
            [incise.core :refer [load-parsers-and-layouts]]
            [clojure.java.io :refer [file resource]]
            [incise.parsers.core :refer :all]))

(describe "register"
  (before (reset! parsers {}))
  (it "can register parsers to extensions"
    (should-not-throw (register ["markdown"] (fn [_])))))

(describe "parse"
  (before-all (load-parsers-and-layouts))
  (with real-md-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (it "outputs html"
    (parse @real-md-file)))

(run-specs)
