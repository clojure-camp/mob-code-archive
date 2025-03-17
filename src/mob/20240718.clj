(ns mob.20240718
  (:require [clojure.string :as string]
            [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

(defn move-count [instructions]
  (loop [instructions instructions
         index 0
         steps 0]
    (if (<= 0 index (dec (count instructions)))
      (recur (update instructions index inc) 
             (+ index (nth instructions index)) 
             (inc steps))
      steps)))

#_(->> (slurp "resources/aoc/2017day5.txt")
      (string/split-lines)
      (mapv parse-long)
      move-count)
             
;; aoc 2017 day 5

(rcf/tests
  (move-count [0 3 0 1 -3]) := 5)

;; Now, the jumps are even stranger: after each jump, if the offset was three or more, instead decrease it by 1. Otherwise, increase it by 1 as before.

(defn move-count-2 [instructions]
  (loop [instructions instructions
         index 0
         steps 0]
    (if (<= 0 index (dec (count instructions)))
      (let [offset (nth instructions index)]
       (recur (update instructions index
                      (if (< offset 3)
                        inc
                        dec))
              (+ index offset)
              (inc steps)))
      steps)))

(rcf/tests
 (move-count-2 [0 3 0 1 -3]) := 10)

#_(->> (slurp "resources/aoc/2017day5.txt")
       (string/split-lines)
       (mapv parse-long)
       move-count-2
       time)
;; 13.5 seconds    

(defn move-count-2-transient [instructions]
  (loop [instructions (transient instructions)
         index 0
         steps 0]
    (if (<= 0 index (dec (count instructions)))
      (let [offset (nth instructions index)]
        (recur (assoc! instructions index
                       (if (< offset 3)
                         (inc offset)
                         (dec offset)))
               (+ index offset)
               (inc steps)))
      steps)))

#_(->> (slurp "resources/aoc/2017day5.txt")
       (string/split-lines)
       (mapv parse-long)
       move-count-2-transient
       time)
;; 6.5 seconds


(defn move-count-2-atom [instructions]
  (let [instructions' (volatile! (transient instructions))
        index (volatile! 0)
        steps (volatile! 0)]
    (while (<= 0 @index (dec (count @instructions')))
      (let [offset (nth @instructions' @index)]
        (vreset! instructions'
               (assoc!
                @instructions'
                @index
                (if (< offset 3)
                 (inc offset)
                 (dec offset))))
        (vswap! index + offset)
        (vswap! steps inc)))
    @steps))
      
    
  
  
#_(move-count-2-atom [0 3 0 1 -3])  
  
#_(->> (slurp "resources/aoc/2017day5.txt")
       (string/split-lines)
       (mapv parse-long)
       move-count-2-atom
       time)

;; 14 seconds

(defn move-count-2-java-array [instructions]
  (let [instructions' (int-array instructions)
        length (alength instructions')
        index (volatile! 0)
        steps (volatile! 0)]
    (while (<= 0 @index (dec length))
      (let [offset (aget instructions' @index)]
        (aset-int
         instructions'
         @index
         (if (< offset 3)
           (unchecked-inc offset)
           (unchecked-dec offset)))
        (vswap! index + offset)
        (vswap! steps inc)))
    @steps))

#_(move-count-2-java-array [0 3 0 1 -3])

#_(->> (slurp "resources/aoc/2017day5.txt")
       (string/split-lines)
       (mapv parse-long)
       move-count-2-java-array
       time)

;; 5 sec
