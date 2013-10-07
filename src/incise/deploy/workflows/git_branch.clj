(ns incise.deploy.workflows.git-branch
  (:require [incise.once :refer [once]]
            [incise.deploy.core :refer [register]]
            [clj-time.core :as tc]
            [clojure.java.io :refer [file]]
            (clj-jgit [porcelain :refer :all :exclude [with-repo]]
                      [querying :refer [commit-info find-rev-commit
                                        create-tree-walk]])
            [clojure.java.shell :refer [with-sh-dir sh]]))

(declare ^:dynamic *repo*)
(declare ^:dynamic *out-dir*)
(declare ^:dynamic *work-dir*)

(def ^:private ^:const subdir "_incise")
(defn git-sub-dir-from-repo [repo]
  (-> repo
      (.getRepository)
      (.getDirectory)
      (file subdir)))

(defn work-tree [repo]
  (-> repo
      (.getRepository)
      (.getWorkTree)))

(defmacro with-repo [path & body]
  `(binding [~'*repo* (load-repo ~path)]
     (binding [~'*out-dir* (git-sub-dir-from-repo ~'*repo*)
               ~'*work-dir* (work-tree ~'*repo*)]
       (with-sh-dir *work-dir*
         ~@body))))

(defn- branch-exists?
  [branch]
  (contains? (git-branch-list *repo*) branch))

(defn checkout-orphaned-branch [branch]
  (sh "git checkout --orphan" branch)
  (git-rm *repo* "."))

(defn setup-branch
  "Setup the given branch if it does not already exist and check it out."
  [branch]
  ((if (branch-exists? branch)
    checkout-orphaned-branch
    (partial git-checkout *repo*)) branch))

(defn- once-in-out-dir []
  (.mkdirs *out-dir*)
  (once :out-dir (.getPath *out-dir*)))

(defn- head-info []
  (commit-info
    (find-rev-commit *repo*
                     (create-tree-walk *repo*)
                     "HEAD")))

(defn- remove-out-dir
  "Remove the *out-dir* prefix from the given path."
  [path]
  (->> path
       (drop (count *out-dir*))
       (apply str)))

(defn move-to-work-dir
  "Move the given file to the working tree directory."
  [output-file]
  (.renameTo output-file
             (file (str *work-dir*
                        (remove-out-dir (.getPath output-file))))))

(defn add-files [files]
  (doseq [afile files]
    (git-add *repo* (.getPath afile))))

(defn deploy
  "Deploy to the given branch. Follow options for commit and push behaviour."
  [{:keys [path branch commit push]
    :or {path "."
         branch "gh-pages"
         commit true
         push true}}]
  {:pre [(string? branch)]}
  (with-repo path
    (let [{source-commit-hash :id} (head-info)
          output-files (once-in-out-dir)]
      (setup-branch)
      (->> output-files
          (map move-to-work-dir)
          (add-files))
      (when commit
        (git-commit *repo* (str "Built from " source-commit-hash
                                " at " tc/now))
        (when push nil)))))

(register :git-branch deploy)
