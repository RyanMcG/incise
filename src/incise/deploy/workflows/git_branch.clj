(ns incise.deploy.workflows.git-branch)

(defn setup-branch [branch])

(defn deploy [{:keys [branch commit push]}]
  {:pre [(string? branch)]}
  (setup-branch)
  (when commit
    (when push nil)))
