(defproject incise "0.2.0"
  :description "A hopefully simplified static site generator in Clojure."
  :url "http://www.ryanmcg.com/incise/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[incise-markdown-parser "0.1.0"]
                 [incise-git-deployer "0.1.0"]
                 [incise-base-hiccup-layouts "0.1.0"]
                 [incise-core "0.2.0"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[incise-stefon "0.1.0"]]}}
  :repl-options {:init-ns incise.repl}
  :main incise.core)
