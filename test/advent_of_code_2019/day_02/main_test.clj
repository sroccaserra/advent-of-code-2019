(ns advent-of-code-2019.day-02.main-test
  (:require [clojure.test :refer :all]
            [advent-of-code-2019.day-02.main :refer :all]))

(deftest reading-ints-from-input
  (testing "parsing a string of coma-separated ints"
    (is (= [1 2 99] (read-ints-from-line "1,2,99")))))
