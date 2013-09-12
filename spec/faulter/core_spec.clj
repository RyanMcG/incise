(ns faulter.core-spec
  (:require [faulter.core :refer :all]
            [speclj.core :refer :all]))

(describe "faulter"
  (with nil-faults (faulter [[odd? "it should be odd"]
                             [number? "it should be a number"]]))
  (it "Always returns a sequence"
    (should (sequential? (@nil-faults nil)))
    (should (sequential? (@nil-faults 3737))))
  (it "is empty when all predicates pass"
    (should (empty? (@nil-faults 3))))
  (it "is a sequence of error messages if it does not pass."
    (should= (list "it should be odd") (@nil-faults 2))
    (should-contain "it should be a number" (@nil-faults nil))))

(describe "faults"
  (with validations [[odd? "it should be odd"]
                     [number? "it should be a number"]])
  (it "finds faults"
    (should= (list "it should be odd" "it should be a number")
             (faults @validations nil))
    (should= (list "it should be odd") (faults @validations 2))
    (should= (list) (faults @validations 3))))

(describe "faulty? and faultless?"
  (with validations [[odd? "it should be odd"]
                     [number? "it should be a number"]])
  (it "complements"
    (should (faulty? @validations 2))
    (should-not (faultless? @validations 2))
    (should-not (faulty? @validations 1))
    (should (faultless? @validations 1))))

(describe "faultless!"
  (with validations [[odd? "it should be odd"]
                     [number? "it should be a number"]])
  (it "throws an error when faults are found"
    (should-throw AssertionError "it should be odd, it should be a number"
                  (faultless! @validations nil))
    (should-throw AssertionError "Invalid odd-number: it should be odd"
                  (faultless! 'odd-number @validations 2)))
  (it "does nothing when no faults are found"
    (should-not-throw (faultless! @validations 3))))

(run-specs)
