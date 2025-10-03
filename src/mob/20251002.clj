(ns mob.20251002
  (:require
   [clojure.string :as str]))

;; aoc 2019 day 4

(def range-input "264360-746325")

;; create a list of numbers in the range - range
;; filter by a validation function - filter, custom fn
;;   helpers: contains same adjacent?, always increasing?
;; count - count
;; maybe also try with: some, loop

(defn doubled-digit?
  "Returns true, if the given number has 2 of the same digit in a row"
  [num]
  (->> (partition 2 1 num)
       (map set)
       (some #(= 1 (count %)))
       boolean))

;; instead, comparing numbers
(defn doubled-digit?
  "Returns true, if the given number has 2 of the same digit in a row"
  [num]
  (->> (partition 2 1 num)
       (some (fn [[a b]] (= a b)))
       boolean))

;; same, but using partial:
(defn doubled-digit?
  "Returns true, if the given number has 2 of the same digit in a row"
  [num]
  (->> (partition 2 1 num)
       (some (partial apply =))
       boolean))

(doubled-digit? "12446") ;; true
(doubled-digit? "12346") ;; false
(doubled-digit? "12444") ;; true

(defn strict-doubled-digit?
  "Returns true, if the given number has 2 of the same digit in a row (but not more than 2!)"
  [num]
   (->> (partition 3 1 num)
        (some (fn [[a b c]] (and (= a b) (not= b c))))
        boolean))

;; TODO this function is broken
(defn strict-doubled-digit?
  "Returns true, if the given number has 2 of the same digit in a row (but not more than 2!)"
  [num]
  (->> (partition 4 1 (concat [nil] num))
       (some (fn [[a b c d]] (and (= b c) (not= a b) (not= c d))))
       boolean))

(strict-doubled-digit? "12446") ;; true
(strict-doubled-digit? "12346") ;; false
(strict-doubled-digit? "12444") ;; false
(strict-doubled-digit? "55444") ;; true
(strict-doubled-digit? "54441") ;; false

(defn never-decrease?
  "Returns true if the given number has each consecutive digit increasing or staying the same"
  [num]
  (->> (partition 2 1 num)
       (every? (fn [[a b]]
                 ;; ascii codes for numbers are in the right order
                 (<= (int a) (int b))))))

;; using compare
(defn never-decrease?
  "Returns true if the given number has each consecutive digit increasing or staying the same"
  [num]
  (->> (partition 2 1 num)
       (every? (fn [[a b]]
                 (not (neg? (compare b a)))))))

(never-decrease? "12345") ;; true
(never-decrease? "11111") ;; true
(never-decrease? "50") ;; false

(defn valid-password-count 
  [valid-password?]
  (->>
   ;; "1234-5678"
   (str/split range-input #"-")
   ;; ("1234" "5678")
   (map parse-long)
   ;; (1234 5678)
   (apply range)
   ;; (1234 1235 ...)
   (map str)
   ;; ("1234" "1235" ...)
   (filter valid-password?)
   (count)))

(defn part-1 []
  (valid-password-count
   (fn [num]
     (and (doubled-digit? num)
          (never-decrease? num)))))

#_(part-1) ;; 945

(defn part-2 []
  (valid-password-count
   (fn [num]
     (and (strict-doubled-digit? num)
          (never-decrease? num)))))

#_(part-2) ;; 617


