(ns incise.parsers.helpers-spec
  (:require [speclj.core :refer :all]
            [clj-time.core :refer [date-time]]
            [clojure.java.io :refer [file]]
            (incise.parsers [core :refer [map->Parse]]
                            [helpers :refer :all])))

(describe "date-str->path"
  (it "handles empty dates well"
    (should= "" (date-str->path "")))
  (it "basically works"
    (should= "2013/8/12/" (date-str->path "2013-08-12"))))

(describe "Parse->path"
  (it "works with dated parses"
    (should= "2013/8/12/something-about-binding-pry/index.html"
             (Parse->path (map->Parse {:title "Something about binding.pry"
                                       :date "2013-8-12"
                                       :extension "/index.html"}))))
  (it "works with undated parses"
    (should= "run-a-muck/index.html"
             (Parse->path (map->Parse {:title "Run a Muck"
                                       :date ""
                                       :extension "/index.html"})))
    (should= "nilnilnil/index.html"
             (Parse->path (map->Parse {:title "NilNilNil"
                                       :date nil
                                       :extension "/index.html"})))))
(describe "extension"
  (with coffee-file (file "boom/my/coolio.js.coffee"))
  (it "gets the extension of a file"
    (should= "coffee" (extension @coffee-file)))

  (with md-file (file "my.articles/wow.pants.MARKDOWN"))
  (it "enforces a lower-case extension"
    (should= "markdown" (extension @md-file))))

(run-specs)
