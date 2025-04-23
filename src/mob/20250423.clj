(ns mob.20250423
  (:require
   [clojure.string :as str]))

;; who's here?

;; raf
;; don
;; thomas
;; tim
;; mike




;; advent of code 2023 day 8

;; thinking about the problem...

;; potential ways to store the graph:

{:AAA [:BBB :CCC]}

{:AAA {\L :BBB
       \R :CCC}
 :BBB {\L :DDD
       \R :EEE}}

{[:AAA :left] :BBB
 [:AAA :right] :CCC}

;; can use cycle() for the instructions

#_(take 10 (cycle "LF"))
#_(nth (cycle "LF") 20)

;; alternatively, could do something with modulo

;; don't know how many step
;; so will need to use something like loop/recur
;; or reduce on the cycle of steps, with reduced stopping

;; plan:

;; parse instructions and network
;; do the reduce

(def input (slurp "resources/aoc/2023day8.txt"))

(defn parse-input-data [data]
  (let [[instructions _ & network] (str/split-lines data)]
    {:instructions instructions
     :network (->> network 
                   ;; each line is: GLJ = (QQV, JTL)
                   (map #(re-matches #"(...) = \((...), (...)\)" %))
                   #_(map #(re-seq #"\w+" %))
                   (map (fn [[_ source left right]]
                          [source
                           {\L left
                            \R right}]))
                   (into {}))}))

(defn part-1 [data]
  (let [{:keys [instructions network]} (parse-input-data data)]
    (reduce
     (fn [{:keys [location step-count]} instruction]
       (if (= "ZZZ" location)
         (reduced step-count)
         {:location (get-in network [location instruction])
          :step-count (inc step-count)}))
     {:location "AAA"
      :step-count 0}
     (cycle instructions))))

#_(part-1 input)