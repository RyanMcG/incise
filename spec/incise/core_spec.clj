(ns incise.core-spec
    (:require [speclj.core :refer :all]
              [incise.core :refer :all]))

(describe "namespace is layout or parser filter"
  (it "returns false on non-parser and non-layout namespaces"
    (doseq [ns-sym ['arbitrary.name.space 'my.parser.yay 'your.layout.boop]]
      (should-not (namespace-is-layout-or-parser? ns-sym))))
  (it "returns true for parser and layout namespaces"
    (doseq [ns-sym ['incise.parsers.cool-thing 'incise.layouts.bootstrapped]]
      (should (namespace-is-layout-or-parser? ns-sym)))))

(describe "namespace is spec or test"
  (it "returns true for specs and tests"
    (doseq [ns-sym ['cool.thing-test 'incise.parsers.core-spec]]
      (should (namespace-is-spec-or-test? ns-sym))))
  (it "returns false for other namespaces"
    (doseq [ns-sym ['cool.thing 'incise.parsers.batman]]
      (should-not (namespace-is-spec-or-test? ns-sym)))))

(describe "finding parsers and layouts"
  (with layout-and-parser-syms (find-parser-and-layout-symbols))
  (it "does not contain core namespaces"
    (doseq [core-sym core-namespace-symbols]
      (should-not-contain core-sym @layout-and-parser-syms)))
  (it "does not contain spec namespaces"
    (doseq [spec-sym ['incise.parsers.core-spec
                      'incise.parsers.markdown-spec]]
      (should-not-contain spec-sym @layout-and-parser-syms)))
  (it "contains default layouts and parsers"
    (doseq [default-sym ['incise.parsers.markdown
                         'incise.layouts.page]]
      (should-contain default-sym @layout-and-parser-syms))))

(run-specs)
