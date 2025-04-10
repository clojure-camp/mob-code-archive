(ns mob.20250410
  (:require
   [clojure.string :as str]))

;; https://adventofcode.com/2023/day/6

(defn distance [total-time held-time]
  (* held-time (- total-time held-time)))

#_(distance 7 0) ;; 0
#_(distance 7 1) ;; 6
#_(distance 7 2) ;; 10
#_(distance 7 3) ;; 12

(defn possible-distances [total-time]
  (->> (range 1 total-time)
       (map (partial distance total-time))))

#_(possible-distances 5)
#_(possible-distances 6)
#_(possible-distances 7)
#_(possible-distances 9)

(defn win-count [time distance]
  (->> (possible-distances time)
       (filter #(> % distance))
       count))

#_(win-count 7 9)

(defn part-1 []
  (->> (slurp "resources/aoc/2023day6sample.txt")
       (str/split-lines)
       (map (fn [line] (str/split line #"\s+")))
       ;; (["Time:" "7" "15" "30"] ["Distance:" "9" "40" "200"])
       (map rest)
       ;; (("7" "15" "30") ("9" "40" "200"))
       (apply map vector)
       ;; (["7" "9"] ["15" "40"] ["30" "200"])
       (map (fn [pair]
              (map parse-long pair)))
       ;; [[7 9] [15 40] [30 200]]
       (map (fn [[time distance]]
              (win-count time distance)))
       (apply *)))

#_(part-1)

(defn part-2 []
  (->> (slurp "resources/aoc/2023day6sample.txt")
       (str/split-lines)
       (map (fn [line] (str/split line #"\s+")))
     ;; (["Time:" "7" "15" "30"] ["Distance:" "9" "40" "200"])
       (map rest)
     ;; (("7" "15" "30") ("9" "40" "200"))
       (map (fn [v]
              (apply str v)))
     ;; ("71530" "940200")
       (map parse-long)
     ;; (71530 940200)
       (apply win-count)))

#_(part-2)

;; above is slow for the real problem, but good nough
;; there are algorithmic improvements we could make to win-count
;; (there is probably a closed-form math formula)
