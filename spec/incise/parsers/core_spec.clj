(ns incise.parsers.core-spec
  (:require [speclj.core :refer :all]
            (incise [load :refer [load-parsers-and-layouts]]
                    [config :as conf])
            [clojure.java.io :refer [file resource]]
            [incise.parsers.core :refer :all]))

(describe "register"
  (before (reset! parsers {}))
  (it "can register parsers to extensions"
    (should-not-throw (register ["markdown"] (fn [_])))))

(describe "parsers"
  (before-all (reset! parsers {}))
  (it "is initially empty"
    (should (empty? @parsers)))
  (it "gets populted when parsers are loaded"
    (load-parsers-and-layouts)
    (doseq [extension ["htm" "html" "markdown" "md" "hiccup"]]
      (should-contain extension @parsers))))

(describe "parse"
  (before-all
    (conf/merge {:out-dir "/tmp/incise-specs"})
    (load-parsers-and-layouts))
  (with real-md-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (with output-file (parse @real-md-file))
  (it "outputs html"
    (should (.exists @output-file))
    (should-contain #"<html>" (slurp @output-file))))

(run-specs)
