{:layout :page
 :path "index.html"}

(require '[incise.parsers.impl.markdown :refer [markdown-to-html]])
(require '[hiccup.core :refer [html]])
(require '[hiccup.element :refer [link-to]])
(require '[clojure.string :refer [replace-first]])

(def md-source (slurp "README.md"))
(def svg-logo (slurp "assets/logo.svg"))
(def svg-logo-link
  (html (link-to "https://github.com/RyanMcG/incise" svg-logo)))

(-> md-source
    (markdown-to-html)
    (replace-first #"\<img.*/\> " svg-logo-link))
