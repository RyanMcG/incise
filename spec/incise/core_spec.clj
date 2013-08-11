(ns incise.core-spec
    (:require [speclj.core :refer :all]
              [incise.core :refer :all]))

(describe "namespace is layout or parser filter"
  (it "returns false on non-parser and non-layout namespaces"
    (doseq [ns-sym ['arbitrary.name.space 'my.parser.yay 'your.layout.boop]]
      (should-not (namespace-is-layout-or-parser ns-sym))))
  (it "returns true for parser and layout namespaces"
    (doseq [ns-sym ['incise.parsers.cool-thing 'incise.layouts.bootstrapped]]
      (should (namespace-is-layout-or-parser ns-sym))))
  (it "returns false for core parser and layout namespaces"
    (doseq [core-ns core-namespace-symbols]
      (should-not (namespace-is-layout-or-parser core-ns)))))
