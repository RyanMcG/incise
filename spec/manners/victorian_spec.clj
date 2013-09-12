(ns manners.victorian-spec
  (:require [manners.victorian :refer :all]
            [speclj.core :refer :all]))

(describe "manners"
  (with nil-coach (coach [[odd? "it should be odd"]
                           [number? "it should be a number"]]))
  (it "Always returns a sequence"
    (should (sequential? (@nil-coach nil)))
    (should (sequential? (@nil-coach 3737))))
  (it "is empty when all predicates pass"
    (should (empty? (@nil-coach 3))))
  (it "is a sequence of error messages if it does not pass."
    (should= (list "it should be odd") (@nil-coach 2))
    (should-contain "it should be a number" (@nil-coach nil))))

(describe "bad-manners"
  (with validations [[odd? "it should be odd"]
                     [number? "it should be a number"]])
  (it "finds bad-manners"
    (should= (list "it should be odd" "it should be a number")
             (bad-manners @validations nil))
    (should= (list "it should be odd") (bad-manners @validations 2))
    (should= (list) (bad-manners @validations 3))))

(describe "rude? and proper?"
  (with validations [[odd? "it should be odd"]
                     [number? "it should be a number"]])
  (it "complements"
    (should (rude? @validations 2))
    (should-not (proper? @validations 2))
    (should-not (rude? @validations 1))
    (should (proper? @validations 1))))

(describe "avow!"
  (with validations [[odd? "it should be odd"]
                     [number? "it should be a number"]])
  (it "throws an error when bad manners are found"
    (should-throw AssertionError "it should be odd, it should be a number"
                  (avow! @validations nil))
    (should-throw AssertionError "Invalid odd-number: it should be odd"
                  (avow! 'odd-number @validations 2)))
  (it "does nothing when no bad manners are found"
    (should-not-throw (avow! @validations 3))))

(run-specs)
