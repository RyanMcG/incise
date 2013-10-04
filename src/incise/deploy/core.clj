(ns incise.deploy.core
  (:require [incise.config :as conf])
  (:refer-clojure :exclude [get]))

(defonce workflows (atom {}))

(defn get [& args]
  (apply clojure.core/get @workflows args))

(defn deploy
  "Deploy using the user's specified workflow."
  [& {:as config}]
  (conf/merge config)
  (let [{workflow-name :workflow :as settings} (conf/get :deploy)
        workflow (get workflow-name)]
    (if workflow
      (workflow settings)
      (throw (RuntimeException. (str "No workflow registered as " workflow-name))))))

(defn register
  "Register a deployment workflow."
  [workflow-name workflow]
  (swap! workflows
         assoc workflow-name workflow))
