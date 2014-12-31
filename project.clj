(defproject incise "0.5.0"
  :description "A hopefully simplified static site generator in Clojure."
  :url "http://www.ryanmcg.com/incise/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[incise-markdown-parser "0.2.0"]
                 [incise-git-deployer "0.1.0"]
                 [incise-base-hiccup-layouts "0.3.0"]
                 [incise-core "0.5.0"]]
  :source-paths []
  :profiles {:dev {:dependencies [[com.ryanmcg/incise-vm-layout "0.5.0"]
                                  [com.ryanmcg/incise-codox "0.2.0"]]
                   :source-paths ["src"]}}
  :repl-options {:init-ns incise.repl}
  :aliases {"incise" ^:pass-through-help ["run" "-m" "incise.core"]}
  :main incise.core)
