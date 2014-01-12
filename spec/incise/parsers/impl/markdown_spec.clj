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

(describe "parsing"
  (around-with-custom-config :in-dir "resources/spec"
                             :out-dir "/tmp/")
  (with markdown-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (with parse (comp slurp first force (html-parser markdown-to-html)))
  (it "parses a markdown file into html"
    (should-contain #"<html>" (@parse @markdown-file))))

(run-specs)
