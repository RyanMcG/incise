(ns incise.deploy.workflows.git-branch-spec
  (:require [stefon.util :refer [temp-dir]]
            [speclj.core :refer :all]
            (clojure.java [io :refer [file]]
                         [shell :refer [with-sh-dir]])
            [clj-jgit [porcelain :refer :all :exclude [with-repo]]]
            [incise.deploy.workflows.git-branch :refer :all])
  (:import [org.eclipse.jgit.api Git]))

(def spec-temp-dir (partial temp-dir "incise-deploy-git-branch-spec"))

(defn- create-dummy-repo
  "Create a git repository in a temporary directory."
  []
  (let [tmp-dir (spec-temp-dir)
        repo (git-init tmp-dir)]
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
      (should= @file-name (#'incise.deploy.workflows.git-branch/remove-out-dir
                            (.getCanonicalPath (file @out-dir @file-name))))))
  (context "move-to-work-dir"
    (it "moves a file from out-dir to work-dir"
      (should= (file @work-dir @file-name)
               (move-to-work-dir @ex-file)))))

(run-specs)
