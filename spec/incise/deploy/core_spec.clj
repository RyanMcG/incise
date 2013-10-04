(ns incise.deploy.core-spec
  (:require [speclj.core :refer :all]
            [incise.deploy.core :refer :all])
  (:refer-clojure :exclude [get]))

(describe "register and get:"
  (before (reset! workflows {}))
  (with workflow-name :my-cool-workflow)
  (with dummy-func (fn [_]))
  (context "register"
    (it "registers a development workflow"
      (should-not-throw (register @workflow-name @dummy-func))
      (should-contain @workflow-name @workflows)))
  (context "get"
    (before (register @workflow-name @dummy-func))
    (it "finds a registered workflow by name"
      (should= @dummy-func (get @workflow-name)))))

(run-specs)
