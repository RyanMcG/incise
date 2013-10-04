(ns incise.deploy.workflows.git-branch-spec
  (:require stefon.util
            [speclj.core :refer :all]
            (clojure.java [io :refer [file]]
                         [shell :refer [with-sh-dir]])
            [clj-jgit [porcelain :refer :all :exclude [with-repo]]]
            [incise.deploy.workflows.git-branch :refer :all])
  (:import [org.eclipse.jgit.api Git]))

(defn- create-dummy-repo
  []
  (let [tmp-dir (stefon.util/temp-dir "incise-deploy-git-branch-spec")
        repo (git-init tmp-dir)]
    (git-commit repo "Initial commit.")
    tmp-dir))

(defn- same-file?
  "Compare the canonical paths of the two file like values."
  [afile-like bfile-like]
  (= (.getCanonicalPath (file afile-like))
     (.getCanonicalPath (file bfile-like))))

(describe "with-repo"
  (with repo-dir (create-dummy-repo))
  (around [it] (with-repo @repo-dir (it)))
  (it "binds *repo*, *out-dir* and *work-dir*"
    (doseq [bvar [#'*repo* #'*out-dir* #'*work-dir*]]
      (should (bound? bvar))))
  (it "binds *repo* to a Git instance"
    (should (instance? Git *repo*)))
  (it "binds *work-dir* to the working tree directory"
    (should (same-file? @repo-dir *work-dir*)))
  (it "binds *out-dir* to a directory nested within the git dir."
    (should (same-file? (file @repo-dir ".git/_incise") *out-dir*))))

(run-specs)
