(ns mob.20221209
  (:require
   [hyperfiddle.rcf :refer [tests]]
   [com.rpl.specter :as x]
   [clojure.string :as string]))

;; aoc 2022 day 1

(hyperfiddle.rcf/enable!)

(defn parse [path]
  (-> (slurp path)
      (string/split #"\n\n")
      (->> (map (fn [x]
                  (reduce + (map (fn [y] (Integer. y))
                                 (string/split x #"\n"))))))))

#_(->> (slurp "resources/day1example.txt")
       (x/transform [] #(string/split % #"\n\n"))
       (x/transform [x/ALL] #(string/split % #"\n"))
       (x/transform [x/ALL x/ALL] #(Integer. %))
       (x/transform [x/ALL] #(reduce + %))
       (sort)
       (take-last 3)
       (reduce +))

(defn x-transform [v f]
  (first v))

#_(x-transform [x/ALL] #(reduce + %))

#_(x/select [x/ALL x/ALL] [["1000" "2000" "3000"] ["4000"] ["5000" "6000"] ["7000" "8000" "9000"] ["10000"]])

(defn day1part1
  [path]
  (->> (parse path)
       (apply max)))

(defn day1part2
  [path]
  (->> (parse path)
       (sort)
       (take-last 3)
       (reduce +)))

(tests
 (day1part1 "resources/aoc/2022day1example.txt") := 24000
 (day1part2 "resources/aoc/2022day1example.txt") := 45000)

#_(day1part1 "resources/aoc/2022day1input.txt")
#_(day1part2 "resources/aoc/2022day1input.txt")
