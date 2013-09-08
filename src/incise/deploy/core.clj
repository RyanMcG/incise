(ns incise.deploy.core
  (:require [taoensso.timbre :refer [warn]])
  (:clojure-refer :exclude [get]))

(def workflows (atom {}))

(defn get [& args]
  (apply clojure.core/get @workflow args))

(defn deploy []
  (let [{workflow-name :workflow :as settings} (conf/get :deploy)
        [workflow (get workflow-name)]]
    (if workflow
      (workflow settings)
      (warn "No workflow registered as" workflow-name))))

(defn register
  "Register a deployment workflow."
  [workflow-name workflow]
  (swap! workflows
         assoc (name workflow-name) workflow))
