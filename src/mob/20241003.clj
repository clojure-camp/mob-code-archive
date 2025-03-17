(ns mob.20241003
  (:require 
   [clojure.string :as string]))

;; aoc 2019 day 6

;; read input
;; parse and create some graph representation

{:COM nil
 :B :COM
 :C :B}

{:COM {:B {:G {:H {}}
           :C {}}}}

{:COM 0
 :B 1
 :C 2}

;; count-parents(graph, :A) - problable involves recursion
;; loop over each item with count-parents(), sum +

(defn count-parents [graph node]
  (if-let [parent (graph node)]
    (+ 1 (count-parents graph parent))
    0))

(def count-parents-memoized
  (memoize
   (fn [graph node]
     (if-let [parent (graph node)]
       (+ 1 (count-parents-memoized graph parent))
       0))))

(defn count-parents-safe
  ;; can this memoized?
  ;; https://stackoverflow.com/questions/9898069
  [graph n node]
  (if-let [parent (graph node)]
    (recur graph (inc n) parent)
    n))

#_(count-parents
   {:L :K
    :I :D
    :F :E
    :D :C
    :B :COM
    :J :E
    :C :B
    :E :D
    :G :B
    :H :G
    :K :J} 
   :L
   0)

(defn parse-file [filename]
  (->> filename
       slurp
       string/split-lines
       (map #(string/split % #"\)"))
       (map (fn [[parent child]]
              [(keyword child) (keyword parent)]))
       (into {})))

(defn compute-total-orbits [graph]
  (->> graph
       keys
       (map (partial count-parents-safe graph 0))
       (apply +)))

#_(->> "resourcs/aoc/2019day6sample.txt"
       parse-file
       compute-total-orbits)

;; could maybe use walk instead? https://clojuredocs.org/clojure.walk