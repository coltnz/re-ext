(ns coltnz.re-ext-test
  (:require [clojure.test :refer :all]
            [coltnz.re-ext :as re])
  (:import (java.util.regex Pattern)))


(deftest and-test
  (is (re/= (re/and #"a" #"b") #"(ab)"))
  (is (re/= (re/and #"a" (re-pattern "b")) #"(ab)"))
  (is (re/= (re/and #"\d" (re-pattern "\\w.*") #"(\t)") #"(\d\w.*(\t))"))
  (def def-re #"bcd")
  (is (re/= (re/and #"\d" def-re) #"(\dbcd)"))
  (let [local-re #"bcd"]
    (is (re/= (re/and #"a" local-re #"e" local-re) #"(abcdebcd)"))
    (is (re/= (re/and def-re local-re) #"(bcdbcd)")))
  (is (re/= (re/and #"a" def-re #"e" def-re) #"(abcdebcd)"))
  (is (re/= (re/and #"a" (re/and #"b")) #"(a(b))"))
  (is (re/= (re/and #"a" (re/and #"b" #"c")) #"(a(bc))"))
  (is (re/= (re/and #"a" (re/and #"b" (re-pattern "d")))
            #"(a(bd))")))

(deftest or-test
  (is (re/= (re/or #"a" #"b") #"(a|b)"))
  (is (re/= (re/or #"a" (re-pattern "b")) #"(a|b)"))
  (is (re/= (re/or #"\d" (re-pattern "\\w.*") #"(\t)") #"(\d|\w.*|(\t))"))
  (def def-re #"bcd")
  (let [local-re #"bcd"]
    (is (re/= (re/or #"a" local-re #"e" local-re) #"(a|bcd|e|bcd)"))
    (is (re/= (re/or def-re local-re) #"(bcd|bcd)")))
  (is (re/= (re/or #"a" def-re #"e" def-re) #"(a|bcd|e|bcd)"))
  (is (re/= (re/or #"a" (re/or #"b") #"c") #"(a|(b)|c)"))
  (is (re/= (re/or #"a" (re/or #"b" #"c")) #"(a|(b|c))"))
  (is (re/= (re/or #"a" (re/or #"b" (re-pattern "c") (re-pattern "d")))
            #"(a|(b|c|d))")))

(deftest and-or-test
  (is (re/= (re/or #"a" (re/and #"b")) #"(a|(b))")))

