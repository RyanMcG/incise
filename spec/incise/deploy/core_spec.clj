(ns incise.deploy.core-spec
  (:require [speclj.core :refer :all]
            [incise.config :as conf]
            [incise.spec-helpers :refer :all]
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

(describe "deploy"
  (with workflow-name :my-cool-workflow)
  (with dummy-deploy-text "DEPLOY!!1")
  (with dummy-deploy (fn [_] @dummy-deploy-text))
  (around-with-custom-config :deploy {:workflow @workflow-name})
  (before (register @workflow-name @dummy-deploy))
  (it "deploys when the workflow exists"
    (should= @dummy-deploy-text (deploy)))
  (it "throws an error when the workflow does not exist"
    (let [bad-workflow-name "derpderp"]
      (should-throw RuntimeException
                    (str "No workflow registered as " bad-workflow-name)
                    (deploy :deploy {:workflow bad-workflow-name})))))

(run-specs)
