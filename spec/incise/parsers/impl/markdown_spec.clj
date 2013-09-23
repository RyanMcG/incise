(ns incise.parsers.impl.markdown-spec
  (:require [speclj.core :refer :all]
            [clojure.java.io :refer [file resource]]
            (incise [load :refer (load-parsers-and-layouts)]
                    [config :as conf])
            [incise.parsers.impl.markdown :refer :all]
            [me.raynes.cegdown :as md])
  (:import [java.io File]))

(describe "parsing"
  (before-all (conf/merge {:in-dir "resources/spec"
                           :out-dir "/tmp/"}))
  (with markdown-file (file (resource "spec/another-forgotten-binding-pry.md")))
  (it "does something"
    (should-not-throw (parser @markdown-file))))

(run-specs)
