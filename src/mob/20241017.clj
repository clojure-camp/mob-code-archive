(ns mob.20241017 
  (:require [clojure.string :as str]))

;; aoc 2020 day 5

;; BFFFBBFRRR: row 70, column 7, seat ID 567.
;; FFFBBBFRRR: row 14, column 7, seat ID 119.
;; BBFFBBFRLL: row 102, column 4, seat ID 820.

;; split each line
;; for each line, get row and column
;; do some math afterwards

;; "BFFFBBFRRR" => {:row 70 :column 7} => id (70*8+7) 

;; reduce   
;;     ~= accumulator pattern

;; loop

(defn boarding-passes [all-boarding-passes-input-str]
  (str/split all-boarding-passes-input-str #"\n"))

(boarding-passes "BFFFBBFRRR\nFFFBBBFRRR\nBBFFBBFRLL")

(defn translate-char [character]
  ({\F 0 \B 1 \L 0 \R 1} character))

(def binary-to-int #(Integer/parseInt % 2))

(defn parse-pass
  [boarding-pass]
  (->> boarding-pass
       (map translate-char)
       str/join
       binary-to-int))

#_(parse-pass "BFFFBBFRRR")

(defn eval-boarding-passes [all-boarding-passes-input-str]
  (->> all-boarding-passes-input-str
       str/split-lines
       (map parse-pass)
       (apply max)))

#_(eval-boarding-passes "BFFFBBFRRR\nFFFBBBFRRR\nBBFFBBFRLL")

#_(eval-boarding-passes (slurp "resources/aoc/2020day5.txt"))