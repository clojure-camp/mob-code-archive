(ns mob.20211202
  (:require
    [clojure.string :as string]
    [clojure.set :as set]))

;; byr
; iyr
; eyr
; hgt
; hcl
; ecl
; pid
;

;; split
;; regex
;; grammar

{:passport/birth-year "..."
 :passport/height "..."}


(def name-lookup {"byr" :passport/birth-year
                  "iyr" :passport/issue-year
                  "eyr" :passport/expiration-year
                  "hgt" :passport/height
                  "hcl" :passport/hair-colour
                  "ecl" :passport/eye-colour
                  "pid" :passport/passport-id
                  "cid" :passport/country-id})

(def value-validators {:passport/birth-year #"\d{4}"
                       :passport/issue-year #"\d{4}"
                       :passport/expiration-year #"\d{4}"
                       :passport/height #"\d+(in|cm)"
                       :passport/hair-colour #"#[a-f0-9]{6}"
                       :passport/eye-colour #"[a-z]{3}"
                       :passport/passport-id #"\d{9}"
                       :passport/country-id #"\d{3}"})



(def required-fields (set (vals name-lookup)))

(defn parse-passport [s]
  (apply merge (map (fn [token] (let [[k v] (string/split token #":")] {(name-lookup k) v}))
                    (string/split s #" |\n"))))

(defn parse-passport [s]
  (set/rename-keys (into {} (map (fn [token] (string/split token #":"))
                                 (string/split s #" |\n")))
                   name-lookup))


#_(parse-passport "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048889\nhcl:#cfa07d byr:1929")

;;;;;;

(defn valid-kv? [[k v]]
  (boolean (re-matches (k value-validators) v)))

(defn valid? [passport]
  (and (set/subset?
         (set (keys passport))
         required-fields)
       (every? valid-kv? passport)))


#_(valid? (parse-passport "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048889\nhcl:#cfa07d byr:1929"))
#_(valid? (parse-passport "hcl:#ae17e1 iyr:2013\neyr:2024\necl:brn pid:760753108 byr:1931\nhgt:179cm\ncid:123"))

#_(let [filename "aoc2020day4.txt" required-fields ["byr" "iyr" "eyr" "hgt" "hcl" "ecl" "pid"]]
     (map valid? (map parse-passport (string/split (slurp filename) #"\n\n"))))











(+ 1 2)

(defn aoc1 [l]
  (count
    (filter true?
      (map (fn [a b]
            (< a b))
        (butlast l)
        (rest l)))))

(defn aoc1 [l]
  (count
    (filter (fn [[a b]] (< a b))
      (partition 2 1 l))))

(defn aoc1b [l]
  (reduce (fn [memo [a b]])

          (partition 2 1 l)))

(def aa {:acc 0})

(defn f [a b]
  (if (< a b)
    (merge)))


(defn aoc1c [l]
  (:total
    (reduce (fn [acc item]
              {:total
               (if (< (:prev-item acc) item)
                 (+ (:total acc) 1)
                 (:total acc))
               :prev-item item})
            {:total 0
             :prev-item (first l)}
            (rest l))))


(defn aoc1c [l]
  (:total
    (reduce (fn [{:keys [total prev-item]} item]
              {:total
               (if (< prev-item item)
                 (inc total)
                 total)
               :prev-item item})
            {:total 0
             :prev-item (first l)}
            (rest l))))

#_(aoc1c [199
          200
          208
          210
          200
          207
          240
          269
          260
          263])

(partition 2 1 [199
                200
                208
                210
                200
                207
                240
                269
                260
                263])
