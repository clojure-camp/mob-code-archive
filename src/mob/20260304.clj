(ns mob.20260304 
  (:require
   [clojure.set :as set]))

;; goal 1: a fn in clj that a grid and returns the next state of the grid based on conway's game of life

;; goal 2: run it continously in the terminal

;; PLANNING

;; what are neighbors?
;;   8 adjacent cells (including diagonals), outside-of-bounds is dead

;; how do we represent the grid?

;;   2d vector
;;   vector           would need to also store width somewhere
;;   {[x y] 1/0}   
;;   #{[x y]}    <=== we'll try this for kicks (sparse representation)

;; how to keep track of size of grid?

;;  going to attempt infinite grid

;;  how do we represent value of a cell?
;;   1/0  
;;   true/false?  
;;   :alive/:dead    
;;   :alive/nil     

;;   whether coordinates exists in set or not <==== this one

;; what functions might we need? (inputs, and outputs)

;; next-state(grid) => grid

;; random-grid() => grid

;; run(grid, n-steps) => grid

;;  cell->neighbors-coordinates(cell-coridinates) => [coordinates]

;;  cell->neighbors(grid, cell-coordinates) => [:alive nil :alive nil]

;;  cell->neighbor-alive-count(grid, coordinates) => number

;;  all-coordinates(grid)

;;  coordinates-of-dead-cells-to-check-if-reproduce(grid)


;; IMPLEMENTATION

;; stable square
(def alives #{[0 0] [0 1] [1 0] [1 1]})

;; horiztonal-vertical flipper
(def alives #{[0 0] [0 1] [0 2]})

(def deltas
  (for [x [1 0 -1]
        y [1 0 -1]
        :when (not (= 0 x y))]
    [x y]))

;;    -1   0   +1
;; -1  x   x   x
;;  0  x       x
;; +1  x   x   x

(defn neighbor-coordinates [[x y]]
  (map (fn [[dx dy]] [(+ x dx) (+ y dy)]) deltas))

(defn alive-neighbor-count [[x y] alives]
  (->> (neighbor-coordinates [x y])
       (filter alives)
       count))

(alive-neighbor-count [0 0] alives)

(defn alive-next? [alive? n-count]
  (or (and alive? (<= 2 n-count 3))
      (and (not alive?) (= n-count 3)))

  ;; original, simplified to minimum above
  #_(cond
    ;; Any live cell with fewer than two live neighbours dies, as if by underpopulation.
    (and alive? (< n-count 2)) false 
    ;; Any live cell with two or three live neighbours lives on to the next generation.
    (and alive? (<= 2 n-count 3)) true
    ;; Any live cell with more than three live neighbours dies, as if by overpopulation.
    (and alive? (> n-count 3)) false
    ;; Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
    (= n-count 3) true
    ;; Otherwise, dead cells stay dead
    :else false))

(defn next-state [alives]
  (let [survivors (->> alives
                       (filter (fn [xy]
                                 (alive-next? true (alive-neighbor-count xy alives))))
                       set)
        births    (-> (mapcat neighbor-coordinates alives)
                      set
                      ;; above might have coordinates of alive ones, so subtract those
                      (set/difference alives)
                      (->> (filter (fn [xy]
                                     (alive-next? false (alive-neighbor-count xy alives)))))
                      set)]
    (into survivors births)))

(next-state alives)


;; homework: display this (is it correct?)




