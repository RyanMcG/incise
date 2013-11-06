(ns incise.parsers.core-spec
  (:require [speclj.core :refer :all]
            [incise.config :as conf]
            (incise [load :refer [load-parsers-and-layouts]])
            [clojure.java.io :refer [file resource]]
            [incise.parsers.core :refer :all]))

(describe "register"
  (before (reset! parsers {}))
  (with parser (fn []))
  (it "can register parsers to extensions"
    (should-not-throw (register [:mkd "markdown"] @parser))
    (should-contain "markdown" @parsers)
    (should-contain "mkd" @parsers))
  (it "can register parsers to extensions"
    (should-not-throw (register ["markdown"] @parser)))
  (it "can register parser to extension"
    (should-not-throw (register "markdown" @parser)))
  (it "can register parsers to keyword extensions"
    (should-not-throw (register [:markdown] @parser))))

(describe "register-mappings"
  (before (reset! parsers {}))
  (with parser (fn []))
  (with mappings {:markdown :mkd
                  :txt [:rst :thing]})
  (before (register :markdown @parser))
  (before (register :txt @parser))
  (it "register mappings copies parsers to new extensions"
    (register-mappings @mappings)
    (doseq [parser-key (map name [:mkd :thing :rst])]
      (should-contain parser-key @parsers)
      (should= @parser (@parsers parser-key)))))

(describe "parsers"
  (before-all (reset! parsers {}))
  (it "is initially empty"
    (should (empty? @parsers)))
  (it "gets populted when parsers are loaded"
    (load-parsers-and-layouts)
    (doseq [extension ["htm" "html" "markdown" "md" "clj"]]
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
