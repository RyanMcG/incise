(ns incise.parsers.html-spec
  (:require [incise.parsers.html :refer :all]
            [incise.parsers.parse :refer [map->Parse]]
            [clj-time.coerce :refer [to-date]]
            [clojure.java.io :refer [file resource]]
            [speclj.core :refer :all]))

(describe "File->Parse"
  (with short-md-file (file (resource "spec/short-sample.md")))
  (it "reads some stuff out of a file, yo"
    (should= (map->Parse {:title "My Short Page"
                          :layout :page
                          :date (to-date "2013-08-12")
                          :path "2013/8/12/my-short-page/index.html"
                          :tags [:cool]
                          :category :blarg
                          :publish true
                          :content "\n\nHey there!\n"
                          :extension "/index.html"})
             (File->Parse identity @short-md-file))))

(run-specs)
