(ns mob.20250206 
  (:require
   [clojure.string :as str]))

;; Consider the word "abode" . We can see that the letter a is in position 1 and b is in position 2. In the alphabet, a and b are also in positions 1 and 2. Notice also that d and e in abode occupy the positions they would occupy in the alphabet, which are positions 4 and 5.

;; Given an array of strings, return an array of the number of letters that occupy their positions in the alphabet for each word. For example,

;; solve (["abode","ABc","xyzD"]) = [4, 3, 1]

#_(- \b \a)

(int \b)

(char 98)
(str/index-of "abcde" "c")

(first [:a :b :c])
(nth [:a :b] 0)
(get [] 1)
(get [:a :b] 0)

(defn is-letter-in-its-alphabetical-index 
  "Function accepts tuple with index and character and returns true if charc..."
  [[i c]]
  (= i (- (int c) (int 97))))

(defn count-of-letters-in-their-index [w]
  (->> w
       #_(map identity)
       ;; compare the position of the character to position in the english alphabet
       (map-indexed vector)
       (map is-letter-in-its-alphabetical-index)
       (filter true?)
       (count)
       ))

(defn is-letter-in-its-alphabetical-index-2
  "Function accepts tuple with index and character and returns true if charc..."
  [i c]
  (= i (- (int c) (int 97))))

(defn count-of-letters-in-their-index-2 [w]
  (->> w
       #_(map identity)
       ;; compare the position of the character to position in the english alphabet
       (map-indexed is-letter-in-its-alphabetical-index-2)
       (filter true?)
       (count)))

(map-indexed vector "abcdef")

(map is-letter-in-its-alphabetical-index (map-indexed vector "abzdef") )

(map identity "abc")

(defn solve
  [words] 
  (->> words
       (map str/lower-case)
       (map count-of-letters-in-their-index )
       (vec)
       ))

(defn solve2
  [words]
  (->> words
       (map str/lower-case)
       (map count-of-letters-in-their-index-2)
       (vec)))


(solve ["abode","ABc","xyzD"])

(solve2 ["abode","ABc","xyzD"])

(solve ["for" "it" "waS"])

(def alphabet "abcdefghijklmnopqrstuvwxyz")

(defn check
  [alphabet word]
  (->> (map = alphabet (str/lower-case word))
       (filter true?)
       count))

(check alphabet "abode")

(map (partial check alphabet) ["abode" "ABc" "xyzD" "ABCD"])