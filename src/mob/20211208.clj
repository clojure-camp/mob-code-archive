(ns mob.20211208
  (:require [clojure.string :as string]))

;; aoc 2021

;; day 1 part 1 a
#_(-> (slurp "resources/aoc/2021day1.txt")
      (string/split #"\n")
      (->>
       (map (fn [str] (Integer. str)))
       (reduce (fn [memo item]
                 {:count (+ (memo :count)
                            (if (and (memo :prev-item)
                                     (> item (memo :prev-item)))
                              1
                              0))
                  :prev-item item})
               {:count 0
                :prev-item nil})))


;; day 1 part 1 b
#_(let [numbers (-> (slurp "resources/aoc/2021day1.txt")
                    (string/split #"\n")
                    (->> (map (fn [str] (Integer. str)))))]
    (count (filter true? (map < numbers (rest numbers)))))

;; day 1 part 2
#_(-> (slurp "resources/aoc/2021day1.txt")
      (string/split #"\n")
      (->>
        (map (fn [str] (Integer. str)))
        (partition 3 1)
        (map (partial apply +))
        (partition 2 1)
        (map (partial apply <))
        (filter true?)
        count))

;; day 2 part 1
#_(-> (slurp "resources/aoc/2021day2.txt")
      (string/split #"\n")
      (->>
           (map (fn [instruction]
                 (let [[direction amount] (string/split instruction #" ")]
                   [(keyword direction) (Integer. amount)])))
           (reduce (fn [memo [direction amount]]
                    (case direction
                      :forward (update memo :horizontal-position + amount)
                      :down (update memo :depth + amount)
                      :up (update memo :depth - amount)))
                   {:depth 0
                    :horizontal-position 0})
           ((fn [{:keys [depth horizontal-position]}]
             (* depth horizontal-position)))))

;; day 2 part 2
#_(-> (slurp "resources/aoc/2021day2.txt")
      (string/split #"\n")
      (->>
           (map (fn [instruction]
                 (let [[direction amount] (string/split instruction #" ")]
                   [(keyword direction) (Integer. amount)])))
           (reduce (fn [memo [direction amount]]
                    (case direction
                      :forward (-> memo
                                  (update :horizontal-position + amount)
                                  (update :depth + (* amount (memo :aim))))
                      :down (update memo :aim + amount)
                      :up (update memo :aim - amount)))
                   {:aim 0
                    :depth 0
                    :horizontal-position 0})
           ((fn [{:keys [depth horizontal-position]}]
             (* depth horizontal-position)))))

;; day 3 part 1
#_(-> (slurp "resources/aoc/2021day3.txt")
      (string/split #"\n")
      (->> #_(take 5)
           (map (fn [binary-string]
                  (map (fn [bit-string] (Integer. bit-string))
                       (string/split binary-string #""))))
           (apply map vector) ;; tranpose
           (map frequencies)
           (map (partial sort-by second))
           (map (partial map first))
           (apply map vector) ;; transpose
           (map (partial apply str))
           (map (fn [x] (Integer/parseInt x 2)))
           (apply *)))

;; day 3 part 1 b
#_(-> (slurp "resources/aoc/2021day3.txt")
      (string/split #"\n")
      (->>
           (map (fn [binary-string]
                  (map (fn [bit-string] (case bit-string
                                          "0" -1
                                          "1" 1))
                       (string/split binary-string #""))))
           (reduce (fn [memo i]
                     (map + memo i)))
           (map (fn [x] (if (pos? x) 1 0)))
           ((fn [x]
             (* (Integer/parseInt (apply str x) 2)
                (Integer/parseInt (apply str (map (fn [i] (if (zero? i) 1 0)) x)) 2))))))


;; day 3 part 2
#_(let [find-rating (fn [index comparator bit-strings]
                      (if (= 1 (count bit-strings))
                        (first bit-strings)
                        (let [choosing-function (fn [groups]
                                                   (if (comparator (count (groups \1)) (count (groups \0)))
                                                    [0 (groups \0)]
                                                    [1 (groups \1)]))
                              [first-bit grouped-binary-strings] (->> (group-by #(get % index) bit-strings)
                                                                      choosing-function)]
                          (recur (inc index) comparator grouped-binary-strings))))
        nums (-> (slurp "resources/aoc/2021day3.txt")
                 (string/split #"\n"))
        a (find-rating 0 < nums)
        b (find-rating 0 >= nums)]
    (* (Integer/parseInt a 2)
       (Integer/parseInt b 2)))

;; day 3 part 2
(defn find-most-common-first-bit-tie-breaker-1 [bit-strings]
  (let [f (->> bit-strings
               (map first)
               frequencies)]
    (if (<= (f \0) (f \1))
      \1
      \0)))

(defn find-least-common-first-bit-tie-breaker-0 [bit-strings]
  (let [f (->> bit-strings
               (map first)
               frequencies)]
    (if (<= (f \0) (f \1))
      \0
      \1)))

#_(let [find-rating (fn find-rating [find-target-bit-fn bit-strings]
                      (if (= 1 (count bit-strings))
                        (first bit-strings)
                        (let [target-bit (find-target-bit-fn bit-strings)
                              filtered-bit-strings (filter (fn [s] (= (first s) target-bit)) bit-strings)]
                          (apply str target-bit (f fignitar-dnind-target-bit-fn (map rest filtered-bit-strings))))))
        nums (-> (slurp "resources/aoc/2021day3.txt")
                 (string/split #"\n"))
        a (find-rating find-most-common-first-bit-tie-breaker-1 nums)
        b (find-rating find-least-common-first-bit-tie-breaker-0 nums)]
    (* (Integer/parseInt a 2)
       (Integer/parseInt b 2)))
