(ns mob.20241107
  (:require
   [clojure.set :as set]
   [clojure.string :as str]))

;; Advent of Code - 2020 Day 6

;; PART 1

(-> "resources/aoc/2020day6sample.txt"
    slurp
    (str/split #"\n\n")
    (->>  (map distinct)
          (map (partial remove #{\newline}))
          (map count)
          (apply +)))

(->> (-> "resources/aoc/2020day6sample.txt"
         slurp
         (str/split #"\n\n"))
     (map distinct)
     (map (partial remove #{\newline}))
     (map count)
     (apply +))

(->> (str/split (slurp "resources/aoc/2020day6sample.txt") #"\n\n")
     (map distinct)
     (map (partial remove #{\newline}))
     (map count)
     (apply +))

;; PART 2

(defn handle-group-string [s]
  (->> (str/split s #"\n")
       (map set)
       (apply set/intersection)
       (count))
  )

(handle-group-string "ab\nac") ;; 1

(-> "resources/aoc/2020day6sample.txt"
    slurp
    (str/split #"\n\n")
    (->> (map handle-group-string)
         (apply +)))


;; "abc\n\na\nb\nc\n\nab\nac\n\na\na\na\na\n\nb\n"

;; ["abc" "a\nb\nc" "ab\nac" "a\na\na\na" "b\n"]

;; ((\a \b \c) (\a \newline \b \c) (\a \b \newline \c) (\a \newline) (\b \newline))

;; ((\a \b \c) (\a \b \c) (\a \b \c) (\a) (\b))

;; (3 3 3 1 1)

;; 11

(->> (str/split (slurp "resources/aoc/2020day6sample.txt") #"\n\n")
     (map distinct)
     (map (partial remove #{\newline}))
     (map count)
     (apply +))

(->> (str/split (slurp "resources/aoc/2020day6sample.txt") #"\n\n")
     (map (fn [x] (count (remove #{\newline} (distinct x)))))
     (apply +))


(map (fn [a b c] (+  a b c)) [1 1 1] [2 2 2] [3 3 3])

;; this is transposing?
(map vector [1 2 3] [4 5 6] [7 8 9])