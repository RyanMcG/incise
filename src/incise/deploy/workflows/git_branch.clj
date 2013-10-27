(ns incise.deploy.workflows.git-branch
  (:require [incise.once :refer [once]]
            [incise.deploy.core :refer [register]]
            [clj-time.core :as tc]
            [clojure.java.io :refer [file]]
            (clj-jgit [porcelain :refer :all :exclude [git-push with-repo]]
                      [querying :refer [commit-info find-rev-commit]]
                      [internal :refer [new-rev-walk]])
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

(defn branch-exists?
  [branch]
  (boolean (some #{(str "refs/heads/" branch)}
                 (map #(.getName %) (git-branch-list *repo*)))))

(defn checkout-orphaned-branch [branch]
  (let [{:keys [exit err]} (sh "git" "checkout" "--orphan" branch)]
    (if (= 0 exit)
      (sh "git" "rm" "-rf" "*")
      (throw (RuntimeException. err)))))

(defn setup-branch
  "Setup the given branch if it does not already exist and check it out."
  [branch]
  ((if (branch-exists? branch)
     (partial git-checkout *repo*)
     checkout-orphaned-branch) branch))

(defn- once-in-out-dir []
  (.mkdirs *out-dir*)
  (once :out-dir (.getPath *out-dir*)))

(defn- head-info []
  (when-let [commit (find-rev-commit *repo*
                                   (new-rev-walk *repo*)
                                   "HEAD")]
    (commit-info *repo* commit)))

(defn- remove-out-dir
  "Remove the *out-dir* prefix from the given path."
  [path]
  (->> path
       (file)
       (.getCanonicalPath)
       (drop (inc (count (.getCanonicalPath *out-dir*))))
       (apply str)))

(defn move-to-work-dir
  "Move the given file to the working tree directory."
  [from-file]
  (let [to-file (file *work-dir* (remove-out-dir from-file))]
    (.renameTo from-file to-file)
    to-file))

(defn git-push
  "Shell out to git and push the current branch to the given remote and branch."
  [remote branch]
  (let [{:keys [exit err]} (sh "git" "push" "-f" remote branch)]
    (when-not (= exit 0)
      (throw (RuntimeException. err)))))

(defn add-file [afile] (git-add *repo* (.getPath afile)))

(defn deploy
  "Deploy to the given branch. Follow options for commit and push behaviour."
  [{:keys [path remote branch commit push]
    :or {path "."
         remote "origin"
         branch "gh-pages"
         commit true
         push true}}]
  {:pre [(string? branch) (string? remote)]}
  (with-repo path
    (let [{source-commit-hash :id} (head-info)
          start-branch (git-branch-current *repo*)]
      (once-in-out-dir)
      (setup-branch branch)
      (->> *work-dir*
           (file-seq)
           (map move-to-work-dir)
           (map add-file)
           (dorun))
      (when commit
        (git-commit *repo* (str "Built from " source-commit-hash " at "
                                (tc/now)))
        (when push
          (git-push remote branch)
          (git-checkout *repo* start-branch))))))

(register :git-branch deploy)
