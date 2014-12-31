{:path "index.html"
 :title nil}

(require '[incise.parsers.impl.markdown])
(require '[hiccup.core :refer [html]])
(require '[hiccup.element :refer [link-to image]])
(require '[clojure.string :refer [replace-first]])

(def md-source (slurp "README.md"))
(def svg-logo "/assets/images/logo.svg")
(def svg-logo-link
  (html (link-to "https://github.com/RyanMcG/incise"
                 (image {:id "logo"} svg-logo "incise logo"))))

(-> md-source
    (#'incise.parsers.impl.markdown/markdown-to-html)
    (replace-first #"^<h1>.*</h1><p>.*<h2>(.*)</h2>" "<h1>$1</h1>"))
