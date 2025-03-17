(ns mob.20240704
  (:require
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; aoc 2015 day 1 part 1

;; list.forEach(|x| => {
;;  floor = 0
;;  if(x == "(")
;;    floor+=1
;;  else
;;    floor -= 1
;;  end
;;  return floor
;; })

(defn aoc2015day1part1 [input]
  (let [counts (frequencies input)]
    (- (counts \()
       (counts \))))) 
  
(defn aoc2015day1part1 [input]
  (reduce (fn [floor paren]
            (case paren
              \( (inc floor)
              \) (dec floor)))
          0
          input))

(rcf/tests
  (aoc2015day1part1 "())") := -1
  (aoc2015day1part1 ")())())") := -3)

;; aoc 2016 day 1 part 1

;; class Person
;;   currentDirection = N
;;   x = 0
;;   y = 0
;; 
;;   def turn(direction)
;;      currentDirection = ???
;;      N R => E
;;      E R => S
;;      S R => W
;;      W R => N
;;   end
;;
;;   def walk(steps)
;;      update x or y based on currentDirection
;;      N , S adds
;;      W, E subtracts
;;   end
;;
;;   def distance()
;;     calculate based on currentDirection
;;   end

;; converting our input to something nicer to work with
;; "R2, R3" => [\R 2 \R 3] <-- choosing this one
;; "R2, R3" => [[:turn :right] [:walk 2] [:turn :right] [:walk 3]]
;; "R2, R3" => [{\R 2} {\R 3}]

(defn to-instructions [input]
  (re-seq #"R|L|\d" input))

(defn to-instructions [input]
  (remove #{\space \,} input))

(defn to-instructions [input]
  (mapcat (fn [x]
           (case x
             \R [:R]
             \L [:L]
             \, []
            \space []
            [(parse-long (str x))]))
       input))

(defn to-instructions [input]
  (->> input
       (map (fn [x]
              (case x
                \R :R
                \L :L
                \, nil
                \space nil
                (parse-long (str x)))))
       (remove nil?)))

(defn to-instructions [input]
  (keep (fn [x]
          (case x
            \R :R
            \L :L
            \, nil
            \space nil
            (parse-long (str x))))
        input))

#_(to-instructions "R2, R3, L3")

(defn move [state amount]
  (case (:direction state)
    :N (update state :y #(+ % amount))
    :S (update state :y - amount)
    :E (update state :x + amount)
    :W (update state :x - amount)))
  
(rcf/tests
 (move {:x 0 :y 0 :direction :N} 2) := {:x 0 :y 2 :direction :N})
 
(defn turn [state instruction]
  (assoc state
         :direction
         (case [(:direction state) instruction]
           [:N :R] :E
           [:E :R] :S
           [:S :R] :W
           [:W :R] :N
           [:N :L] :W
           [:E :L] :N
           [:S :L] :E
           [:W :L] :S)))
    

(rcf/tests
 (turn {:x 0 :y 0 :direction :N} :R) := {:x 0 :y 0 :direction :E}
 (turn {:x 0 :y 0 :direction :N} :L) := {:x 0 :y 0 :direction :W})

(defn step [state instruction]
  (cond
    (= instruction :R)
    (turn state :R)
    (= instruction :L)
    (turn state :L)
    :else
    (move state instruction)))

(defn step [state instruction]
  (if (= instruction :R)
    (turn state :R)
    (if (= instruction :L)
      (turn state :L)
      (move state instruction))))

(defn step [state instruction]
  (case instruction
    :R (turn state :R)
    :L (turn state :L)
    (move state instruction)))

(rcf/tests
  (step {:x 0 :y 0 :direction :N} :R) := {:x 0 :y 0 :direction :E}
  (step {:x 0 :y 0 :direction :N} :L) := {:x 0 :y 0 :direction :W}
  (step {:x 0 :y 0 :direction :N} 2) := {:x 0 :y 2 :direction :N})

(defn distance [state]
  (+ (Math/abs (:x state))
     (Math/abs (:y state))))

(defn aoc2016day1part1 [input]
  (->> input
       to-instructions
       (reduce step {:x 0
                     :y 0
                     :direction :N})
       distance))
                 
(rcf/tests
 (aoc2016day1part1 "R2, L3") := 5
 (aoc2016day1part1 "R2, R2, R2") := 2
 (aoc2016day1part1 "R5, L5, R5, R3") := 12)
 
;; alternatively, we could have modeled this as:  {:distance 0 :direction 270} 
;; which may have made some of the above fns simpler