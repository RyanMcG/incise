(defproject incise "0.1.0-SNAPSHOT"
  :description "A hopefully simplified static site generator in Clojure."
  :url "https://github.com/RyanMcG/incise"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.0"]
                 [hiccup "1.0.2"]
                 [compojure "1.1.5"]
                 [http-kit "2.1.10"]
                 [robert/hooke "1.3.0"]
                 [me.raynes/cegdown "0.1.0"]
                 [org.clojure/java.classpath "0.2.0"]
                 [org.clojure/tools.nrepl "0.2.3"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [org.clojure/tools.cli "0.2.4"]
                 [pallet-map-merge "0.1.0"]
                 [clj-time "0.5.1"]
                 [clj-jgit "0.4.0"]
                 [com.taoensso/timbre "2.6.1"]
                 [com.ryanmcg/stefon "0.5.0-83b9595399ad6a324895269bfa2cdc0c2c243a48"]
                 [manners "0.2.0"]]
  :profiles {:dev {:dependencies [[speclj "2.9.1"]]}}
  :repl-options {:init-ns incise.repl}
  :plugins [[speclj "2.9.1"]]
  :test-paths ["spec/"]
  :main incise.core)
