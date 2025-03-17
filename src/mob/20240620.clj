(ns mob.20240620
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; reactable? char1 char2

(defn reactive? [a b]
  (and
   a
   b
   (= (str/lower-case a)
      (str/lower-case b))
   (not= a b)))

(reactive? \a \A) := true
(reactive? \a \a) := false
(reactive? \a \B) := false

(defn single-pass [s]
  (reduce (fn [acc c]
            (if (reactive? (peek acc) c)
              (pop acc)
              (conj acc c)))
          []
          s))

(count (single-pass (slurp "resources/aoc/2018day5.txt")))

(peek [])
(str/lower-case nil)
(peek [1 2 3])
(peek '(1 2 3))

(rcf/tests
 (single-pass "aA") := (vec "")
 (single-pass "abBA") := (vec "")
 (single-pass "abAB") := (vec "abAB")
 (single-pass "aabAAB") := (vec "aabAAB")
 (single-pass "dabAcCaCBAcCcaDA") := (vec "dabCBAcaDA")
 (single-pass "dabAaCBAcCcaDA") := (vec "dabCBAcaDA"))

(defn single-pass-recur [result c chars]
  (cond
    (nil? c)
    result
    (reactive? c (first chars))
    (recur result (second chars) (drop 2 chars))
    :else
    (recur (conj result c) (first chars) (rest chars))))

(drop 2 [1 2 3 4])

(rest [1 2 3 4])

(rest [])
(first [])
(single-pass-recur [] \d "abAcCaCBAcCcaDA")

(defn single-pass-recur-loop [chars]
  (loop [result []
         c (first chars)
         chars (rest chars)]
    (cond
      (nil? c)
      result
      (reactive? c (first chars))
      (recur result (second chars) (drop 2 chars))
      :else
      (recur (conj result c) (first chars) (rest chars)))))

;; exercise: update the above to use peek and conj, and be able to complete in one pass
;; currently, relies on multiple

(single-pass-recur-loop "dabAcCaCBAcCcaDA")


;; reduce? 
  ;;  compare last char, to next char
  ;; can we move on safely?

;; recursively
  ;; compare first char with next char
  ;; or
  ;; compare next char with previous char
  ;; also accumulate chars

;; partition 2 1 ?

abcde
[[nil a] [a b] [b B] [B d] [d e]]
[[nil a] [a b] [B d] [d e]]


;; maybe there's a way without multiple passes if the moment we find a reaction, we keep acting in the same spot

;; stack

;; vector - add to back, remove from back
(conj [1 2] 3)
(pop [1 2])

;; list - add to front, remove from front
(conj '(1 2) 3)
(pop '(1 2))

;; queue - add to back, remove from front


(set (str/lower-case (slurp "resources/aoc/2018day5.txt")))



;part  2

;; filter w/ regex

;; list a - z
;;    reduce,  calculate count(reaction(filtered(string))), keep if minimum


;; list a - z
;;   map  count(reaction(filtered(string))) 
;;  sort-by or max-key


(defn try-removal [polymer unit]
  (->> polymer
       (remove (fn [itm] (or (= itm unit) (= itm (Character/toUpperCase unit)))))
       (single-pass)
       count))



(try-removal "dabAcCaCBAcCcaDA" \a)
6

(->> (range (int \a) (inc (int \z)))
     (map char)
     (map (partial try-removal "dabAcCaCBAcCcaDA"))
     (apply min))


(->> (range (int \a) (inc (int \z)))
     (map char)
     (map #(try-removal "dabAcCaCBAcCcaDA" %))
     (apply min))

(->> (range (int \a) (inc (int \z)))
     (map char)
     (map (fn [x] (try-removal "dabAcCaCBAcCcaDA" x)))
     (apply min))

(Character/toUpperCase \a)

(defn slurp2 [f]
  (print "A")
  (println "B")
  (slurp f))

(time (->> (range (int \a) (inc (int \z)))
           (map char)
           (pmap (fn [x] (try-removal (slurp2 "resources/aoc/2018day5.txt") x))) ;; will slurp 26 times
           (apply min)))

(+ 1 2)

(time
 (->>  (range (int \a) (inc (int \z)))
       (map char)
       (pmap (partial try-removal (slurp2 "resources/aoc/2018day5.txt"))) ;; will slurp 1
       (apply min)))

[5 1 6 1 26 1 2 5]

(min 1  1 62 6 1 2 2 5)

(min [1 6 2 61 2 ])

