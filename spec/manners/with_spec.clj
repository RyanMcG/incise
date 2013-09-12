(ns manners.with-spec
  (:require [speclj.core :refer :all]
            [manners.with :refer :all]))

(describe "with-etiquette"
  (with etiquette [[odd? "it is odd"]
                     [#(= 0 (mod % 5)) "it is divisible by 5"]])
  (it "works inside"
    (with-etiquette @etiquette
      (should (proper? 5))
      (should (rude? 9))
      (should-contain "it is divisible by 5" (bad-manners 21))
      (should-throw AssertionError (avow! 19))
      (should-not-throw (avow! 25))))
  (context "works inside"
    (it "works with victorian"
      (with-etiquette @etiquette
      (should (proper? 5))
      (should (rude? 9))
      (should-contain "it is divisible by 5" (bad-manners 21))
      (should-throw AssertionError (avow! 19))
      (should-not-throw (avow! 25))))
    (it "works with modern"
      (with-etiquette @etiquette
      (should (valid? 5))
      (should (invalid? 9))
      (should-contain "it is divisible by 5" (errors 21))
      (should-throw AssertionError (validate! 19))
      (should-not-throw (validate! 25)))))
  (it "does not work outside"
    (should-throw AssertionError (rude? 9))))

(run-specs)
