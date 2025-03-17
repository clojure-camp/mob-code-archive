(ns mob.20240905
     (:require
      [hyperfiddle.rcf :as rcf]
      [clojure.string :as str]))

(rcf/enable!)

;; aoc 2019 day 3

;; manhattan-distance()
;; find-intersections(line-a, line-b)

;; line:
;;  #{[0 0] [0 1] [0 2] ...}

;; set/intersection

;; grid, [[nil :a nil] [nil :a nil]]
;;
;;  {[0 1] :a}


"R8,U5,L5,D3\nU7,R6,D4,L4"

;; parse

[[{:direction :right
   :amount 8}
  {:direction :up
   :amount 5}]
 [{:direction :right
   :amount 8}
  {:direction :up
   :amount 5}]]
 
;; lines

[#{[0 1] [0 2]}
 #{[0 1] [0 2]}]

;; intersection

#{[0 1] [4 3]}

;; closest to 0,0

[0 1]

;; distance

1

(def direction-map
  {\U :up
   \R :right
   \D :down
   \L :left})

(defn manhattan-distance-from-origin
  [[x y]]
  (+ (abs x) (abs y)))

(defn parse-line [line]
  (->> (str/split line #",")
       (map (fn [[a & b]]
              {:direction (direction-map a)
               :amount (parse-long (apply str b))}))))   

(defn parse
  [lines]
  (->> lines
       str/split-lines
       (map parse-line)))

(defn offset
  [[x y] [x-diff y-diff]]
  [(+ x x-diff) (+ y y-diff)])

(defn wire-segment
  [initial-point {:keys [direction amount]}]
  (->> (range (inc amount))
       (map (fn [k]
              (case direction
                :right [k 0]
                :left [(- k) 0]
                :up [0 k]
                :down [0 (- k)])))
       (map (fn [point]
              (offset point initial-point)))))                         

(rcf/tests
 (wire-segment [0 0] {:direction :up :amount 3})
 := [[0 0] [0 1] [0 2] [0 3]]
 (wire-segment [1 1] {:direction :down :amount 3})
 := [[1 1] [1 0] [1 -1] [1 -2]])

(defn to-wire-points
  [instructions]
  (reduce (fn [acc {:keys [direction amount]}]
            (case direction
              :left)) 
          [[0,0]]
          instructions))


(defn hello []
  :world)

(rcf/tests
 (hello) := :world
 (manhattan-distance-from-origin [0 1]) := 1
 (to-wire-points [{:direction :right
                   :amount 4}])
 := [[0 0] [1 0] [2 0] [3 0] [4 0]]
 
 (parse-line "R8,U5,L5,D3") :=
 [{:direction :right
   :amount 8}
  {:direction :up
   :amount 5}
  {:direction :left
   :amount 5}
  {:direction :down
   :amount 3}]
 (parse "R8,U5,L5,D3\nU7,R6,D4,L4") :=
 [[{:direction :right
    :amount 8}
   {:direction :up
    :amount 5}
   {:direction :left
    :amount 5}
   {:direction :down
    :amount 3}]
  [{:direction :up
    :amount 7}
   {:direction :right
    :amount 6}
   {:direction :down
    :amount 4}
   {:direction :left
    :amount 4}]]


 nil) 

