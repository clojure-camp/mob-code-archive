(ns mob.20241205 
  (:require
   [clojure.string :as str]
   [com.rpl.specter :as s]))

(def sample-input 
  "3   4
4   3  
2   5
1   3
3   9
3   3")


(->> sample-input
     (str/split-lines) 
     #_(map (fn [line] (map parse-long (str/split line #"   "))))
     (map (fn [line] (str/split line #"   ")))
     (map #(map parse-long %))
     (apply map vector)
     (map sort)
     (apply map vector)
     (map (partial apply -))
     #_ (apply map -)
     #_(map abs)
     #_(apply +))

(->> sample-input
     (str/split-lines)
     (map (fn [line] (map parse-long (str/split line #"   "))))
     (apply map vector)
     (map sort)
     (apply map -)
     (map abs)
     (apply +))

(update-vals [1 2 3 4] inc)

(->> sample-input
     (str/split-lines)
     #_(map (fn [line] (map parse-long (str/split line #"   "))))
     (map (fn [line] (str/split line #"   ")))
     (map #(map parse-long %))
     )

(map #(update-vals '(5 7 9) inc) [0 1 2])

#_(apply map vector )
(parse-long "1")

; ["3   4" "4   3" "2   5" "1   3" "3   9" "3   3"]
; [["3" "4"] ["4" "3"]...]
; ((3 4) (4 3) (2 5) (1 3) (3 9) (3 3))
; [[3 4 2 1 3 3] [4 3 5 3 9 3]]

(apply map - [[3 4 2 1 3 3] [4 3 5 3 9 3]])


(->> sample-input
     (str/split-lines)
     (map (fn [line] (str/split line #"   ")))
     #_(s/select [s/ALL s/ALL])
     (s/transform [s/ALL s/ALL] parse-long)
     #_(apply map vector)
     #_(map sort)
     #_(apply map -)
     #_(map abs)
     #_(apply +))