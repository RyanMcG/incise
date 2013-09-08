(defproject incise "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [speclj "2.1.2"]
                 [ring "1.2.0"]
                 [hiccup "1.0.2"]
                 [compojure "1.1.5"]
                 [http-kit "2.1.10"]
                 [robert/hooke "1.3.0"]
                 [me.raynes/cegdown "0.1.0"]
                 [org.clojure/java.classpath "0.2.0"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [org.clojure/tools.cli "0.2.4"]
                 [clj-time "0.5.1"]
                 [com.taoensso/timbre "2.6.1"]
                 [com.ryanmcg/stefon "0.5.0-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[speclj "2.5.0"]]}}
  :repl-options {:init-ns incise.repl}
  :plugins [[speclj "2.5.0"]]
  :test-paths ["spec/"]
  :main incise.core)
