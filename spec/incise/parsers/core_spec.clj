(ns incise.parsers.core-spec
  (:require [speclj.core :refer :all]
            [incise.core :refer [load-parsers-and-layouts]]
            [clj-time.core :refer [date-time]]
            [clojure.java.io :refer [file resource]]
            [incise.parsers.core :refer :all]))

(describe "source->output"
  (before-all (load-parsers-and-layouts))
  (with real-md-file (file (resource "posts/another-fogotten-binding-pry.md")))
  (it "outputs html"
    (source->output @real-md-file)))

(run-specs)
