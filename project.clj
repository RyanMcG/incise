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
                 [http-kit "2.1.1"]
                 [watchtower "0.1.1"]
                 [markdown-clj "0.9.29"]
                 [org.clojure/java.classpath "0.2.0"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [dieter "0.4.1"]]
  :plugins [[speclj "2.1.2"]]
  :test-paths ["spec/"]
  :main incise.core)
