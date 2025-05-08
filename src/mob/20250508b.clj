(ns mob.20250508b
  (:require
   [hyperfiddle.rcf :as rcf]
   [criterium.core :as crit]))

(rcf/enable!)

;; aoc 2022 day 6

(defn find-marker 
  [packet-size datastream]
  (->> datastream
       (partition packet-size 1)
       (map distinct)
       (keep-indexed (fn [idx item]
                       (when (= packet-size (count item)) idx)))
       first
       (+ packet-size)))

#_(defn part-1 [datastream]
    (foo 4 datastream))

#_(defn part-2 [datastream]
    (foo 14 datastream))

(def part-1 (partial find-marker 4))
(def part-2 (partial find-marker 14))

(rcf/tests
 (part-1 "mjqjpqmgbljsphdztnvjfqwrcgsmlb")
 := 7
 (part-1 "bvwbjplbgvbhsrlpgdmjqwftvncz")
 := 5
 (part-1 "nppdvjthqldpwncqszvftbrmjlhg")
 := 6
 (part-1 "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")
 := 10
 (part-1 "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")
 := 11
 ;;
 (part-2 "mjqjpqmgbljsphdztnvjfqwrcgsmlb")
 := 19
 (part-2 "bvwbjplbgvbhsrlpgdmjqwftvncz")
 := 23
 (part-2 "nppdvjthqldpwncqszvftbrmjlhg")
 := 23
 (part-2 "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg")
 := 29
 (part-2 "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw")
 := 26)

#_(part-2 (slurp "./resources/aoc/2022day6.txt"))

#_(let [data (slurp "./resources/aoc/2022day6.txt")]
    (crit/bench
     (part-1 data)))