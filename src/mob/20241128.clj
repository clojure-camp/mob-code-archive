(ns mob.20241128 
  (:require
   [clojure.string :as string]))

;; Advent of Code - 2020 Day 3

;; plan:

;; import grid into a 2d array

;; at-location(x,y)
;;  it could handle the repetion

;; reduce, w/ 2 values: tree-count, location
;;     what are we reducing over???
;;           infinite input list?

(def grid
  (->> (slurp "resources/aoc/2020day3sample.txt")
       string/split-lines))

(def grid
  (->> (slurp "resources/aoc/2020day3.txt")
       string/split-lines))

(defn coord 
  [grid x y]
  (let [ys (get grid y)
        total (count ys)]
    (get ys (mod x total))))

(defn tree? 
  [grid x y]
  (= \# (coord grid x y)))

#_(tree? grid 2 0)

(defn count-trees 
  [delta-x delta-y]
  (loop [x 0
         y 0
         tree-count 0]
    (if (< y (count grid))
      (recur (+ x delta-x)
             (+ y delta-y)
             (if (tree? grid x y)
               (inc tree-count)
               tree-count))
      tree-count)))

#_(count-trees 3 1)

;; sample: 7

;; part 2

#_(let [runs [[1 1]
              [3 1]
              [5 1]
              [7 1]
              [1 2]]]
  (->> runs
       (map (fn [[dx dy]]
              (count-trees dx dy)))
       (apply *)))

;; sample: 336
