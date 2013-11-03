(ns incise.deploy.workflows.git-branch
  (:require [incise.once :refer [once]]
            [incise.utils :refer [remove-prefix-from-path]]
            [incise.deploy.core :refer [register]]
            [taoensso.timbre :refer [debug info warn]]
            [clojure.java.io :refer [file]]
            [clojure.string :as s]
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

(defn- force-checkout [branch]
  (sh "git" "checkout" "--force" branch))

(defn setup-branch
  "Setup the given branch if it does not already exist and check it out."
  [branch]
  ((if (branch-exists? branch)
     force-checkout
     checkout-orphaned-branch) branch))

(defn- once-in-out-dir []
  (.mkdirs *out-dir*)
  (once :out-dir (.getPath *out-dir*)))

(defn- head-info []
  (when-let [commit (find-rev-commit *repo*
                                   (new-rev-walk *repo*)
                                   "HEAD")]
    (commit-info *repo* commit)))

(defn remove-out-dir [afile]
  (remove-prefix-from-path *out-dir* afile))

(defn move-to-work-dir
  "Move the given file to the working tree directory."
  [from-file]
  (let [to-file (file *work-dir* (remove-out-dir from-file))]
    (if (.isDirectory from-file)
      (do
        (.mkdir to-file)
        nil)
      (do
        (.renameTo from-file to-file)
        to-file))))

(defn git-push
  "Shell out to git and push the current branch to the given remote and branch."
  [remote branch]
  (let [{:keys [exit err]} (sh "git" "push" "-f" remote branch)]
    (when-not (= exit 0)
      (throw (RuntimeException. err)))))

(defn log-files [files]
  (info "Adding the following files:")
  (doseq [afile files]
    (info " " (.getPath afile)))
  files)

(defn add-file [afile]
  (sh "git" "add" (.getPath afile)))

(defn stash [source-commit-hash]
  (let [stash-message (str "(>'')> incise stashing on " source-commit-hash)
        {:keys [out]} (sh "git" "stash" "create" stash-message)
        reference (when-not (s/blank? out) (s/trim-newline out))]
    (when reference
      (sh "git" "stash" "store" reference))
    reference))

(defn unstash [reference]
  (let [{:keys [exit err]} (sh "git" "stash" "apply" reference)]
    (when (not= exit 0)
      (warn "Failed to apply stash:" err))))

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
    (once-in-out-dir)
    (let [{source-commit-hash :id} (head-info)
          start-branch (git-branch-current *repo*)
          stash-ref (stash source-commit-hash)]
      (when stash-ref
        (debug "Stashed with reference:" stash-ref))
      (setup-branch branch)
      (->> *out-dir*
           (file-seq)
           (rest) ; Skip the out directory
           (map move-to-work-dir)
           (keep identity)
           (log-files)
           (map add-file)
           (dorun))
      (when commit
        (let [commit-msg (str "Built from " source-commit-hash \.)]
          (info "Committing with message:" commit-msg)
          (git-commit *repo* commit-msg))
        (when push
          (info "Pushing to" (str remote \/ branch))
          (git-push remote branch)
          (force-checkout start-branch)
          (when stash-ref (unstash stash-ref)))))))

(register :git-branch deploy)
