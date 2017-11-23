(ns coltnz.re-ext
  (:refer-clojure :exclude (and or =))
  (:require [clojure.string :as str])
  (:import (java.util.regex Pattern)
           (clojure.lang PersistentList Symbol)))

(declare and)
(declare or)

(defn compose* [join-by & re-forms]
  (str
    "("
    (str/join
      join-by
      (reduce
        (fn [re-s re-form]
          (condp clojure.core/= (type re-form)
            Symbol
            (conj re-s re-form)
            PersistentList
            (let [[h & t] re-form
                  resolved-fn (resolve h)]
              (cond
                (clojure.core/= resolved-fn #'coltnz.re-ext/and) (conj re-s (apply compose* "" t))
                (clojure.core/= resolved-fn #'coltnz.re-ext/or) (conj re-s (apply compose* "|" t))
                (clojure.core/= resolved-fn #'clojure.core/re-pattern) (if (not (second t))
                                                                         (conj re-s (first t))
                                                                         (throw (Exception. (str "Unknown regex fn " re-form))))))
            Pattern
            (conj re-s (.pattern ^Pattern re-form))
            (throw (Exception. (str re-form " is not a regex form")))))
        []
        re-forms))
    ")"))

(defn pre-compile? [re-forms]
  (loop [[f & fs] re-forms]
    (let [pc?
          (if (seq? f)
            (pre-compile? f)
            (if (symbol? f)
              (if-let [resolved-fn (resolve f)]
                (cond
                  (clojure.core/= resolved-fn #'coltnz.re-ext/and) true
                  (clojure.core/= resolved-fn #'coltnz.re-ext/or) true
                  :else false))
              true))]
      (if-not pc?
        false
        (if fs (recur fs) true)))))

(defmacro and
  "Joins `re-forms` as a new grouped combined pattern where re-forms are anything that evaluates to java.util.regex.Pattern.
  If all forms are considered evaluateable at macro time then the grouped pattern will be compiled at that time.\"
  e.g.
   (re-and #\"a\" #\"b\") => (ab)"
  [& re-forms]
  (if (pre-compile? re-forms)
    (Pattern/compile (apply compose* "" re-forms))
    `(Pattern/compile (compose* "" ~@re-forms))))

(defmacro or
  "Joins `re-forms` as a new grouped explicit or pattern   where re-forms are anything that evaluates to java.util.regex.Pattern.
  If all forms are considered evaluatable at macro time then the grouped pattern will be compiled at that time.\"
  e.g.
   (re-and #\"a\" #\"b\") => (a|b)"
  [& re-forms]
  (if (pre-compile? re-forms)
    (Pattern/compile (apply compose* "|" re-forms))
    `(Pattern/compile (compose* "|" ~@re-forms))))

(defn pattern?
  "Returns true if x is an instance of java.util.regex.Pattern"
  [x]
  (instance? Pattern x))

(defn =
  "Returns true if (str re1) equals (str re2), false if not. "
  ([re1] true)
  ([re1 re2]
   (clojure.core/and
     (pattern? re1)
     (pattern? re2)
     (clojure.core/= (str re1) (str re2))))
  ([re1 re2 & re-forms]
   (boolean
     (reduce
       (fn [re1 re2]
         (if (clojure.core/and
               (pattern? re1)
               (pattern? re2)
               (clojure.core/= (str re1) (str re2)))
           re2
           (reduced false)))
       re-forms))))


