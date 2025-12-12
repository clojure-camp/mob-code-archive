(ns mob.20251211b
  (:require 
   [clojure.string :as string]
   [criterium.core :as c]
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; resources/aoc/2017day2sample.txt

;; PLAN:

;; slurp
;; seperate into rows - string/split-lines
;; parse into numbers - parse-long
;; [[5 1 9 5] [7 5 3] [2 4 6 8]]
;; for each line, calculate the difference - min max
;; sum up

(defn line-checksum [line]
  (- (apply max line)
     (apply min line)))

(rcf/tests
 (line-checksum [5 1 9 5]) := 8)

(defn parse [s]
  (->> s
       string/split-lines
       (map (fn [line]
              (map parse-long (string/split line #"\s"))))
       ;; ((5 1 9 5) (7 5 3) (2 4 6 8))
       ))

(defn part-1-a [data]
  (->> data
       (map line-checksum)
       (apply +)))

(rcf/tests
 (part-1-a (parse (slurp "resources/aoc/2017day2sample.txt")))
 := 18

 (part-1-a (parse (slurp "resources/aoc/2017day2.txt")))
 := 41887)

;; alternative approach:

(defn apply-f-vector [f lines]
  (map (fn [line] (apply f line)) lines))

(defn part-1-b [data]
  (apply +
         (map -
              (apply-f-vector max data)
              (apply-f-vector min data))))

(rcf/tests
 (part-1-b (parse (slurp "resources/aoc/2017day2sample.txt")))
 := 18

 (part-1-b (parse (slurp "resources/aoc/2017day2.txt")))
 := 41887)

(comment
  ;; data is too small to tell a difference

  (let [data (doall (parse (slurp "resources/aoc/2017day2.txt")))]
    (c/quick-bench (part-1-a data)))

  (let [data (doall (parse (slurp "resources/aoc/2017day2.txt")))]
    (c/quick-bench (part-1-b data)))
  )

;; part 2 

;; need to find all pairs
;; 5 9 2 8
;; [[5 9] [5 2] [5 8] [9 2] [9 8] [2 8]]

(defn line-result [row]
  (first (for [x row
               y row
               :when (not= x y)
               :when (> x y)
               :when (zero? (rem x y))]
           (/ x y))))

(rcf/tests
 (line-result [5 9 2 8])
 := 4)

(defn part-2 [data]
  (->> data
       (map line-result)
       (apply +)))

(rcf/tests
 (part-2 (parse "5 9 2 8\n9 4 7 3\n3 8 6 5"))
 := 9
 (part-2 (parse (slurp "resources/aoc/2017day2.txt")))
 := 226
 )

;; homework challenge: implement line-result with recursion
;;  ie. all possible pairs given a set of numbers