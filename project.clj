(defproject incise "0.2.0-SNAPSHOT"
  :description "A hopefully simplified static site generator in Clojure."
  :url "http://www.ryanmcg.com/incise/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[incise-markdown-parser "0.1.0-SNAPSHOT"]
                 [incise-git-deployer "0.1.0-SNAPSHOT"]
                 [incise-base-hiccup-layouts "0.1.0-SNAPSHOT"]
                 [incise-core "0.2.0-SNAPSHOT"]]
  :repl-options {:init-ns incise.repl}
  :main incise.core)
