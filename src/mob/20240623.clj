(ns mob.20240623
  (:require
   [clojure.string :as str]))

;; AOC 2016 Day 4

;; aaaaa-bbb-z-y-x-123[abxyz]
;; a-b-c-d-e-f-g-h-987[abcde]
;; not-a-real-room-404[oarel]
;; totally-real-room-200[decoy]

(defn checksum [room]
  (->> (frequencies (:room/letters room))
       (sort-by (fn [[k v]] [(- v) k]))
       (take 5)
       (map key)
       (apply str)))

(defn real? [room]
  (= (checksum room)
     (:room/checksum room)))

#_(real? {:room/letters "aaaaabbbzyx"
          :room/checksum "abxyz"
          :room/sector-id 123})

#_(checksum (parse-line "not-a-real-room-404[oarel]"))

(defn parse-line [s]
  (let [[letters_id checksum] (str/split s #"\[|\]")
        id (parse-long (last (str/split letters_id #"-")))
        letters (apply str (butlast (str/split letters_id #"-")))]
    {:room/letters letters
     :room/checksum checksum
     :room/sector-id id}))

(defn parse-line- [s]
  (let [[_ letters sector checksum] (re-matches #"([a-z-]+)-([0-9]+)\[([a-z]+)\]" s)]
    {:room/letters (str/replace letters #"-" "")
     :room/checksum checksum
     :room/sector-id (parse-long sector)}))

#_(parse-line "aaaaa-bbb-z-y-x-123[abxyz]")

{:room/letters "aaaaabbbzyx"
 :room/checksum "abxyz"
 :room/sector-id 123}

(defn part1 []
  (->> (slurp "aoc/2016day4.txt")
       str/split-lines
       (map parse-line)
       (filter real?)
       (map :room/sector-id)
       (apply +)))
  
#_(part1)