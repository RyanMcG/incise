(ns incise.parsers.html-spec
  (:require [incise.parsers.html :refer :all]
            [incise.parsers.core :refer [map->Parse]]
            [clojure.java.io :refer [file resource]]
            [speclj.core :refer :all]))

(describe "File->Parse"
  (with short-md-file (file (resource "spec/short-sample.md")))
  (it "reads some stuff out of a file, yo"
    (should= (map->Parse {:title "My Short Page"
                          :layout :page
                          :date "2013-08-12"
                          :tags [:cool]
                          :category :blarg
                          :content "\n\nHey there!\n"
                          :extension "/index.html"}) (File->Parse identity @short-md-file))))

(run-specs)
