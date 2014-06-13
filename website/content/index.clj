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
    (replace-first #"\<img.*/\> " svg-logo-link))
