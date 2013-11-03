(ns incise.deploy.workflows.git-branch-spec
  (:require [stefon.util :refer [temp-dir]]
            [speclj.core :refer :all]
            (clojure.java [io :refer [file]]
                          [shell :refer [with-sh-dir sh]])
            [clj-jgit [porcelain :refer :all :exclude [with-repo git-push]]]
            [incise.config :as conf]
            [incise.deploy.workflows.git-branch :refer :all])
  (:import [org.eclipse.jgit.api Git]))

(def spec-temp-dir (partial temp-dir "incise-deploy-git-branch-spec"))

(defn- create-dummy-repo
  "Create a git repository in a temporary directory."
  []
  (let [tmp-dir (spec-temp-dir)
        repo (git-init tmp-dir)
        gstring "garbage..."
        gitignore-file (file tmp-dir ".gitignore")
        files [gitignore-file
               (file tmp-dir "y")
               (file tmp-dir "x")]]
    (dorun (map #(spit % gstring) files))
    (spit gitignore-file "/x")
    (git-add repo ".")
    (git-commit repo "Initial commit.")
    tmp-dir))

(describe "with-repo"
  (with repo-dir (create-dummy-repo))
  (around [it] (with-repo @repo-dir (it)))
  (it "binds *repo*, *out-dir* and *work-dir*"
    (doseq [bvar [#'*repo* #'*out-dir* #'*work-dir*]]
      (should (bound? bvar))))
  (it "binds *repo* to a Git instance"
    (should (instance? Git *repo*)))
  (it "binds *work-dir* to the working tree directory"
    (should= @repo-dir *work-dir*))
  (it "binds *out-dir* to a directory nested within the git dir."
    (should= (file @repo-dir ".git/_incise") *out-dir*)))

(describe "move-to-work-dir:"
  (with-all work-dir (spec-temp-dir))
  (with-all out-dir (spec-temp-dir))
  (with-all file-name "my-cool-file")
  (with ex-file (file @out-dir @file-name))
  (around [it] (binding [*work-dir* @work-dir
                         *out-dir* @out-dir]
                 (it)))
  (context "remove-out-dir"
    (it "removes the prefixed out-dir returning a relative path"
      (should= @file-name (remove-out-dir
                            (.getCanonicalPath (file @out-dir @file-name))))))
  (context "move-to-work-dir"
    (it "moves a file from out-dir to work-dir"
      (should= (file @work-dir @file-name)
               (move-to-work-dir @ex-file)))))

(describe "add-file"
  (with repo-dir (create-dummy-repo))
  (around [it] (with-repo @repo-dir (it)))
  (with files (map (partial file @repo-dir)
                   ["a" "b" "c"]))
  (it "adds files"
    (should-not-throw (map add-file @files))))

(defn- file-exists? [file-like]
  (.exists (file *work-dir* file-like)))

(describe "branch-exists?"
  (with-all branch-name "a-branch-name")
  (with repo-dir (create-dummy-repo))
  (around [it] (with-repo @repo-dir (it)))
  (it "should not exist with an inital repo"
    (should-not (branch-exists? @branch-name)))
  (it "should exist after creating the branch"
    (checkout-orphaned-branch @branch-name)
    (git-commit *repo* "Blarg blarg")
    (should (branch-exists? @branch-name))))

(describe "checkout-orphaned-branch"
  (with-all branch-name "testest")
  (with repo-dir (create-dummy-repo))
  (around [it] (with-repo @repo-dir (it)))
  (it "changes the checked out branch"
    (should= "master" (git-branch-current *repo*))
    (checkout-orphaned-branch @branch-name)
    (should= @branch-name (git-branch-current *repo*)))
  (it "removes all version controlled files leaving ignored ones"
    (should (file-exists? ".gitignore"))
    (should (file-exists? "x"))
    (should (file-exists? "y"))
    (checkout-orphaned-branch @branch-name)
    (should-not (file-exists? ".gitignore"))
    (should (file-exists? "x"))
    (should-not (file-exists? "y"))))

(describe "setup-branch"
  (with-all branch-name "stuffs")
  (with repo-dir (create-dummy-repo))
  (around [it] (with-repo @repo-dir (it)))
  (it "creates a new orphaned branch if it does not exist"
    (setup-branch @branch-name)
    (should= @branch-name (git-branch-current *repo*)))
  (it "checks the branch out if it does exist"
    (should-not (branch-exists? @branch-name))
    (checkout-orphaned-branch @branch-name)
    (git-checkout *repo* "master")
    (setup-branch @branch-name)
    (should= @branch-name (git-branch-current *repo*))))

(describe "deploy"
  (with repo-dir (create-dummy-repo))
  (with repo-dir-path (.getCanonicalPath @repo-dir))
  (before (conf/merge {:precompiles []
                       :in-dir @repo-dir-path}))
  (after (reset! conf/config {}))
  (around [it] (with-repo @repo-dir (it)))
  (it "deploys without commit or push"
    (should-not-throw (deploy {:path @repo-dir
                               :commit false
                               :push false})))
  (it "deploys with commit only"
    (should-not-throw (deploy {:path @repo-dir
                               :commit true
                               :push false})))
  (context "with a bare clone"
    (with remote-dir-path (.getCanonicalPath (spec-temp-dir)))
    (before (git-clone @repo-dir-path @remote-dir-path "origin" "master" true))
    (before (doto (-> *repo*
                      (.getRepository)
                      (.getConfig))
              (.setString "remote" "origin" "url" @remote-dir-path)
              (.save)))
    (it "deploys with commit and push"
      (should-not-throw (deploy {:path @repo-dir
                                 :commit true
                                 :push true})))))

(run-specs)
