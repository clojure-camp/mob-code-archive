(ns mob.20231206
  (:require 
   [hyperfiddle.rcf :as rcf]))

;; AOC 2015 Day 3

(rcf/enable!)

;; fn given [0 0]   \>   [0 1]
(defn move [[x y] dir]
  (cond
    (= dir \<) [(dec x) y]
    (= dir \>) [(inc x) y]
    (= dir \^) [x (inc y)]
    (= dir \v) [x (dec y)]))

(comment 
  (move [0 0] \>)
  (move [0 0] \<)
  (move [0 0] \^)
  (move [0 0] \v))



(reduce
 (fn [acc itm]
   (move acc itm))
 [0 0]
 "^>v>")  

(reduce
 move
 [0 0]
 "^>v>>>>>>>>>>>>>>")

(reduce
 (fn [acc itm]
   (conj acc (move (last acc) itm)))
 [[0 0]]
 "^>v>>>>>>>>>>>>>>")



(let [locations (reductions
                 move
                 [0 0]
                 "^v^v^v^v^v")]
  (println (count locations))
  (println (count (set locations)))
  (println (count (distinct locations))))

(defn visited-locations [input]
  (let [locations (reductions
                   move
                   [0 0]
                   input)]
    (count (set locations))))


;; [0 0], maintain a set of coordinates as moving
;;    reduce

(->>
 (reduce
  (fn [acc itm]
   (-> acc
       (assoc :locations (conj (:locations acc) (move (:last-location acc) itm)))
       (assoc :last-location (move (:last-location acc) itm))))
  {:locations #{} :last-location [0 0]}
 ;[[0 0]]
  "^>v>>>>>>>>>>>>>><<<<<<")
 :locations
 count)


(->>
 (reduce
  (fn [acc itm]
    (-> acc
        (update :locations conj (move (:last-location acc) itm))
        (update :last-location move itm))
   {:locations #{} :last-location [0 0]})
  "^>v>>>>>>>>>>>>>><<<<<<")
 :locations
 count)


;; detect cycles maybe?
;;     reduce

(rcf/tests
 (visited-locations ">") := 2
 (visited-locations "^>v<") := 4
 (visited-locations "^v^v^v^v^v") := 2)


#_(visited-locations (slurp "aoc2015day3.txt"))
 
;; ctrl-enter => eval the form
;; alt/option-enter => go to root of this form, eval

(defn reflect [[x y]] [(* x -1) (* y -1)])

(defn visited-locations-2 [input]
  (let [locations (reductions
                   move
                   [0 0]
                   input)
        locations-2 (map reflect locations)]
    (count (set (concat locations locations-2)))))


(visited-locations-2 "^v^v^v^v^v")

#_(visited-locations-2 (slurp "resources/aoc/2015day3.txt"))


(reduce
 (fn [acc itm]
   (conj acc (move (last acc) itm)))
 [[0 0]]
 "^>v>>>>>>>>>>>>>>")

(set (concat [[1 2]] [[-1 -2]]))



(defn visited-locations-3 
  [input]
  (let [santa-moves (map first (partition 2 input))
        robot-moves (map last (partition 2 input))
        santa-locations (reductions move [0 0] santa-moves)
        robot-locations (reductions move [0 0] robot-moves)]
    (count (set (concat santa-locations robot-locations)))))


"^v^v^v^v^v" :=> "^^^^^" "vvvvv"

(map first (partition 2 "^v^v^v^v^v"))
(map last (partition 2 "^v^v^v^v^v"))

(visited-locations-3 "^v^v^v^v^v")

#_(visited-locations-3 (slurp "resources/aoc/2015day3.txt"))