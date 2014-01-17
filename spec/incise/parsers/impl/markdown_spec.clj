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

(defn parse-markdown []
  (let [markdown-file (file (resource "spec/markdown-options.md"))
        parse (comp slurp first force (html-parser markdown-to-html))]
    (parse markdown-file)))

(describe "parsing markdown"
  (context "parsing with default options"
    (around-with-custom-config :in-dir "resources/spec"
                               :out-dir "/tmp/")
    (with result (parse-markdown))
    (it "parses a markdown file into html"
        (should-contain #"<html>" @result))
    (it "parses without hardwraps"
        (should-contain "First line Second line" @result)))
  (context "parsing with custom options"
    (around-with-custom-config :in-dir "resources/spec"
                               :out-dir "/tmp/"
                               :parsers {:markdown {:extensions [:hardwraps]}})
    (with result (parse-markdown))
    (it "parses with hardwraps"
      (should-contain "First line<br/>Second line" @result))))

(run-specs)
