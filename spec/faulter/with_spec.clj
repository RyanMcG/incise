(ns faulter.with-spec
  (:require [speclj.core :refer :all]
            [faulter.with :refer :all]))

(describe "with-validations"
  (with validations [[odd? "it is odd"]
                     [#(= 0 (mod % 5)) "it is divisible by 5"]])
  (it "works inside"
    (with-validations @validations
      (should (faultless? 5))
      (should (faulty? 9))
      (should-contain "it is divisible by 5" (faults 21))
      (should-throw AssertionError (faultless! 19))
      (should-not-throw (faultless! 25))))
  (it "does not work outside"
    (should-throw AssertionError (faulty? 9))))

(run-specs)
