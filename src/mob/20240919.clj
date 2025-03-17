(ns mob.20240919
  (:require
   [clojure.string :as str]
   [clojure.core.reducers :as reducers]
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; aoc 2019 day 4

(defn has-double-digit? [n] ;; for part 1
  (->> (partition 2 1 n)
       (some (fn [[a b]] (= a b)))))

(defn has-exact-double-digit? [n] ;; for part 2
  (->> (partition-by identity n)
       (some #(= 2 (count %)))))

#_(has-exact-double-digit? [1 1 1 2 2 2 3 3 3 3 3 4 4])

(defn increasing? [n]
  (apply <= n))

#_(increasing? [1 2 3 4])

#_(has-double-digit? [1 2 2 3])

(defn digits [n] ;; slow, memory heavy
  (map #(Character/digit % 10) (str n)))

(defn digits [n] ;; ~30x faster
  (loop [x n
         ds '()]
    (let [z (quot x 10)]
      (if (zero? z)
        (conj ds (mod x 10))
        (recur z
               (conj ds (mod x 10)))))))
  
#_(digits 1234)

(defn part-1 [input]
  (->> (str/split input #"-")
       (map parse-long)
       (map + [0 1])
       (apply range)
       (map digits)
       (filter increasing?)
       (filter has-exact-double-digit?)
       count))

(defn part-1 [input] ;; reducers, ~same
  (->> (str/split input #"-")
       (map parse-long)
       (map + [0 1])
       (apply range)
       (reducers/map digits)
       (reducers/filter increasing?)
       (reducers/filter has-exact-double-digit?)
       (reducers/reduce (fn [m _] (inc m)) 0)))

(defn part-1 [input] ;; transducers, ~same
  (->> (str/split input #"-")
       (map parse-long)
       (map + [0 1])
       (apply range)
       (eduction (comp (map digits)
                       (filter increasing?)
                       (filter has-exact-double-digit?)))
       (reduce (fn [m _] (inc m)) 0)))

(defn part-1 [input] ;; transducers, ~same
  (->> (str/split input #"-")
       (map parse-long)
       (map + [0 1])
       (apply range)
       (transduce (comp (map digits)
                        (filter increasing?)
                        (filter has-exact-double-digit?)
                        (map (constantly 1)))
                  + 0)))

(defn part-1 [input] ;; loop, ~50% slower
  (let [[start end] (->> (str/split input #"-")
                         (map parse-long))]
    (loop [x start
           cnt 0]
      (if (<= x end)
        (recur (inc x) (if (let [d (digits x)]
                             (and (increasing? d)
                                  (has-exact-double-digit? d)))
                         (inc cnt)
                         cnt))
        cnt))))
      

#_(part-1 "123456-234567")
#_(time (part-1 "264360-746325"))


