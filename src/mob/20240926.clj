(ns mob.20240926
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [malli.core :as m]
   [malli.transform :as mt]))

;; AOC 2020 Day 4

    ;; byr (Birth Year) - four digits; at least 1920 and at most 2002.
    ;; iyr (Issue Year) - four digits; at least 2010 and at most 2020.
    ;; eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
    ;; hgt (Height) - a number followed by either cm or in:
    ;;     If cm, the number must be at least 150 and at most 193.
    ;;     If in, the number must be at least 59 and at most 76.
    ;; hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
    ;; ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
    ;; pid (Passport ID) - a nine-digit number, including leading zeroes.
    ;; cid (Country ID) - ignored, missing or not.

(def Height
  ;; could reimplement this to make use of custom transformers
  [:fn (fn [s]
         (let [[_ height unit] (re-matches #"(\d+)(cm|in)" s)]
           (case unit
             "cm" (<= 150 (parse-long height) 193)
             "in" (<= 59 (parse-long height) 76))))])

(def Passport
  [:map
   [:byr [:int {:min 1920 :max 2002}]]
   [:iyr [:int {:min 2010 :max 2020}]]
   [:eyr [:int {:min 2020 :max 2030}]]
   [:hgt Height]
   [:hcl [:re #"^#[0-9a-f]{6}$"]]
   [:ecl [:enum "amb" "blu" "brn" "gry" "grn" "hzl" "oth"]]
   [:pid [:re #"^\d{9}$"]]])

(defn step2-valid? [passport]
 (try
   (m/coerce Passport
             passport
             mt/string-transformer)
   (catch Exception _)))
 
(comment
  (step2-valid?
   {:ecl "gry"
    :pid "860033327",
    :eyr "2020",
    :hcl "#fffffd",
    :byr "1937",
    :iyr "2017",
    :cid "147",
    :hgt "183cm"}))

(def required-keys
  #{:ecl :pid :eyr :hcl :byr :iyr :hgt})

(defn valid? [m]
  (empty? (set/difference required-keys
                          (set (keys m)))))
  
(defn valid? [passport]
  ((every-pred :ecl :pid :eyr :hcl :byr :iyr :hgt) passport))

(defn valid? [passport]
  (every? passport required-keys))

(comment
  (valid?
   {:ecl "gry"
    :pid "860033327",
    :eyr "2020",
    :hcl "#fffffd",
    :byr "1937",
    :iyr "2017",
    :cid "147",
    :hgt "183cm"}))
  
(defn parse-passport-input [s]
  (->> (str/split s #"\n\n")
       (map #(str/split % #"[\n ]"))
       (map #(map (fn [s] (str/split s #":")) %))
       (map #(into {} %))
       (map #(update-keys % keyword)))) 

#_(->> (slurp "resources/aoc/2020day4sample.txt")
       parse-passport-input
       (filter valid?)
       count)

#_(->> (slurp "resources/aoc/2020day4.txt")
       parse-passport-input
       (filter step2-valid?)
       count)


