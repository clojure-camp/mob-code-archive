(ns mob.20250521
  (:require
   [clojure.test.check :as tc]
   [clojure.test.check.generators :as gen]
   [clojure.test.check.properties :as prop]))

;; implenting merge-sort

;;              [  1 2 5 1 6 23 6 3 ]
;;                /                \  
;;      [ 1 2 5 1 ]              [ 6 23 6 3 ]
;;      /         \               /       \
;; [ 1 2 ]       [ 5 1 ]     [ 6 23 ]    [ 6 3 ]
;;  /    \       /   \       /    \       /   \
;; [ 1 ] [ 2]  [ 5] [ 1 ]   [ 6] [ 23]   [ 6] [ 3 ]  ;;; <-----
;;  \   /        \  /         \   /        \    /
;;  [ 1 2 ]     [ 1 5 ]      [ 6  23 ]     [ 3  6 ]
;;     \         /                  \          /
;;    [ 1 1 2 5 ]                    [ 3 6 6 23 ]
;;                \                  /
;;                  [ 1 1 2 3 5 6 6 23 ]

;; part 1 - implementing merge
;; merge (sorted-list-a, sorted-list-b) => sorted-list-ab
;; probably use loop[]

(defn merge [x y]
  (loop [acc []
         x x
         y y]
    (if (empty? x)
      (into acc y)
      (if (empty? y)
        (into acc x)
        (if (< (first x) (first y))
          (recur (conj acc (first x)) (rest x) y)
          (recur (conj acc (first y)) x (rest y)))))))

(defn merge [x y]
  (loop [acc []
         x x
         y y]
    (cond
      (empty? x)               (into acc y)
      (empty? y)               (into acc x)
      (< (first x) (first y))  (recur (conj acc (first x)) (rest x) y)
      :else                    (recur (conj acc (first y)) x (rest y)))))

#_(merge [2 3 4 5] [1 6 7 8 9])
#_(merge [2 3] [1 6 7 8 9])

;; part 2 - using merge to implement merge sort
;; if empty or 1 item
;;    return it
;; otherwise
;;     merge ( sort(first half), sort(second half))

(defn merge-sort [col]
  (if (< (count col) 2)
    col
    (let [[x y] (split-at (/ (count col) 2) col)]
      (merge (merge-sort x)
             (merge-sort y)))))

#_(merge-sort [5 1 2 6 8 9])

(def prop-result-is-equal-input
  (prop/for-all [x (gen/vector gen/small-integer)]
                (= (count x) (count (merge-sort x)))))
#_(tc/quick-check 100 prop-result-is-equal-input)

(def prop-result-all-ascend
  (prop/for-all [x (gen/not-empty (gen/vector gen/small-integer))]
                (apply <= (merge-sort x))))
#_(tc/quick-check 100 prop-result-all-ascend)

(def prop-result-all-present
  (prop/for-all [x (gen/not-empty (gen/vector gen/small-integer))]
                (= (frequencies x) (frequencies (merge-sort x)))))
#_(tc/quick-check 100 prop-result-all-present)


