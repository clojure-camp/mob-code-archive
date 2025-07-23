(ns mob.20250723 
  (:require
   [clojure.string :as str]))

;; part 1

;; planning

;; parse input into some reasonable clj data struscture
;; ["123456" "789012"]
;; do the filtering problem asks for
;; "123456"
;; do the final math
;;   frequencies
;;   count(1) * count(2)  => 1

;; implement

#_(let [size (* 6 25)
        result (->> (slurp "./resources/aoc/2019day8.txt")
                    (partition size)
                    (apply min-key (fn [chs] (count (filter #{\0} chs))))
                    (frequencies))]
    (* (get result \1)
       (get result \2)))

;; variation - destructuring

#_(let [size (* 6 25)
        {ones \1 twos \2} (->> (slurp "./resources/aoc/2019day8.txt")
                               (partition size)
                               (apply min-key (fn [chs] (count (filter #{\0} chs))))
                               (frequencies))]
    (* ones twos))

;; variation - immediate execution of an anon function

#_(let [size (* 6 25)]
    (->> (slurp "./resources/aoc/2019day8.txt")
         (partition size)
         (apply min-key (fn [chs] (count (filter #{\0} chs))))
         (frequencies)
         ((fn [counts]
            (* (get counts \1)
               (get counts \2))))))

;; variation - juxt

(defn n-count [n]
  (fn [coll]
    (get (frequencies coll) n)))

#_((n-count \1) [\0 \1 \1])

#_(let [size (* 6 25)]
    (->> (slurp "./resources/aoc/2019day8.txt")
         (partition size)
         (apply min-key (n-count \0))
         ((juxt (n-count \1) (n-count \2)))
         (apply *)))

;; variation - juxt + partial

(defn count-n [n coll]
  (get (frequencies coll) n))

;; (partial count-n \0)
;; =>
;; (fn [& args]
;;   (apply count-n \0 args))

#_(let [size (* 6 25)]
    (->> (slurp "./resources/aoc/2019day8.txt")
         (partition size)
         (apply min-key (partial count-n \0))
         ((juxt (partial count-n \1) (partial count-n \2)))
         (apply *)))

;; (apply min-key) ~= (->> (sort-by) (first))

;; variation - "optimization" with memoize frequencies

#_(let [size (* 6 25)
        freqs (memoize frequencies)]
    (->> (slurp "./resources/aoc/2019day8.txt")
         (partition size)
         (apply min-key (fn [chs] (get (freqs chs) \0)))
         (freqs) ;; will reuse a previous freqs calculation
         ((fn [counts]
            (* (get counts \1)
               (get counts \2))))))



;; brief aside on destructuring

(let [{:keys [foo/name bar/name]} {:foo/name "Alice"
                                    :bar/name "Bob"}]
  name) ;; :(

(let [{:foo/keys [name] :bar/keys [name]} {:foo/name "Alice"
                                           :bar/name "Bob"}]
  name) ;; :(

(let [{name-1 :foo/name name-2 :bar/name} {:foo/name "Alice"
                                           :bar/name "Bob"}]
  [name-1 name-2])


;; part 2

;; planning

;v v v v 
[0 2 2 2]
[1 1 2 2]
[2 2 1 2]
[0 0 0 0]
;0 1 1 0

;; transpose could be useful:
;; ~rotating a matrix (2d array)

[[\a \b \c]
 [1 2 3]]

;; =>

[[\a 1]
 [\b 2]
 [\c 3]]

;; implementing:

(defn top-visible-pixel [pixels]
  (-> (drop-while #(= % \2) pixels)
      first))

(defn top-visible-pixel [pixels]
  (some #{\0 \1} pixels))

#_(top-visible-pixel [\0 \1 \2 \1 \0])
#_(top-visible-pixel [\2 \1 \2 \1 \0])

;; transpose

(defn transpose [matrix]
  (apply mapv vector matrix))

#_(apply mapv vector [[1 2 3 4 5] [6 7 8 9 10]])
;; =
#_(mapv vector [1 2 3 4 5] [6 7 8 9 10])

#_(transpose [[1 2 3] [4 5 6]])
;; [[1 4] [2 5] [3 6]]


(defn format-as-image [w pixels]
  (->> pixels
       (map {\0 \space \1 \#})
       (partition w)
       (map (partial apply str))
       (str/join "\n")))

;; the actual part2:

(let [w 2 h 2]
  (->> "0222112222120000"
       (partition (* w h))
       (transpose)
       (map top-visible-pixel)
       (format-as-image w)
       print))

(let [w 25 h 6]
  (->> (slurp "./resources/aoc/2019day8.txt")
       (partition (* w h))
       (transpose)
       (map top-visible-pixel)
       (format-as-image w)
       print))

(let [w 25 h 6]
  (->> (slurp "./resources/aoc/2019day8.txt")
       (partition (* w h))
       (apply map (fn [& column] (top-visible-pixel column)))
       (format-as-image w)
       print))