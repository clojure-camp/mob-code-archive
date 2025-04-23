(ns mob.20250416 
  (:require
   [clojure.string :as str]))

;; https://adventofcode.com/2023/day/7

(defn classify [hand]
  (case (sort > (vals (frequencies hand)))
    [5] :type/five-of-a-kind
    [4 1] :type/four-of-a-kind
    [3 2] :type/full-house
    [3 1 1] :type/three-of-a-kind
    [2 2 1] :type/two-pair
    [2 1 1 1] :type/pair
    [1 1 1 1 1] :type/high-card))

(classify "AAAAA") ;; :type/five-of-a-kind
(classify "AKQJT") ;; :type/high-card
(classify "KJAAA") ;; :type/three-of-a-kind
(classify "TAAAT") ;; :type/full-house

(def types
  [:type/five-of-a-kind
   :type/four-of-a-kind
   :type/full-house
   :type/three-of-a-kind
   :type/two-pair
   :type/pair
   :type/high-card])

(def cards
  (vec "AKQJT987654321"))

;; note: clojure supports sorting vectors
;; (sort [[1 2] [1 3] [2 1] [1 4]])

(defn strength [hand]
  [(.indexOf types (classify hand))
   (vec (for [card hand] (.indexOf cards card)))])

(strength "AAAAA") ; [0 [0 0 0 0 0]]
(strength "AKQJT") ; [6 [0 1 2 3 4]] 

(->> (slurp "resources/aoc/2023day7sample.txt")
     str/split-lines
     (map (fn [line]
            #_(-> (zipmap [:hand :bet]
                          (str/split line #" "))
                  (update :bet parse-long))
            (let [[hand bet] (str/split line #" ")]
              {:hand hand
               :bet (parse-long bet)})))
     ;; [{:hand "32T3K" :bet 765} ...]
     (sort-by (fn [{:keys [hand bet]}]
                (strength hand)))
     reverse
     (map-indexed (fn [index {:keys [hand bet]}]
                    (* (inc index) bet)))
     (apply +))

;; 6440