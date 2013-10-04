(ns incise.deploy.core
  (:require [incise.config :as conf]
            [taoensso.timbre :refer [fatal]])
  (:refer-clojure :exclude [get]))

(defonce workflows (atom {}))

(defn get [& args]
  (apply clojure.core/get @workflows args))

(defn deploy []
  (let [{workflow-name :workflow :as settings} (conf/get :deploy)
        workflow (get workflow-name)]
    (if workflow
      (workflow settings)
      (fatal "No workflow registered as" workflow-name))))

(defn register
  "Register a deployment workflow."
  [workflow-name workflow]
  (swap! workflows
         assoc (name workflow-name) workflow))
