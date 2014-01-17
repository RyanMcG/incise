(ns incise.parsers.impl.markdown-spec
  (:require [speclj.core :refer :all]
            [clojure.java.io :refer [file resource]]
            (incise [load :refer (load-parsers-and-layouts)]
                    [config :as conf])
            [incise.spec-helpers :refer :all]
            [incise.parsers.html :refer [html-parser]]
            [incise.parsers.impl.markdown :refer :all]
            [me.raynes.cegdown :as md])
  (:import [java.io File]))

(describe "parsing markdown"
  (before (load-parsers-and-layouts))
  (with markdown-file (file (resource "spec/markdown-options.md")))
  (around-with-custom-config :in-dir "resources/spec"
                             :out-dir "/tmp/")
  (with parse (comp slurp first force (html-parser markdown-to-html)))
  (with result (@parse @markdown-file))
  (context "parsing with default options"
    (it "parses a markdown file into html"
        (should-contain #"<html>" @result))
    (it "parses without hardwraps"
        (should-contain "First line Second line" @result)))
  (context "parsing with custom options"
    (around-with-custom-config :parsers {:markdown {:extensions [:hardwraps]}})
    (it "parses with hardwraps"
      (should-contain "First line<br/>Second line" @result))))

(run-specs)
