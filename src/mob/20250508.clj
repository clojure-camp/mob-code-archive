(ns mob.20250508
  (:require
   [clojure.set :as set]
   [clojure.string :as str]))

;; exploring different threading macros

(defn message
  [s]
  (-> s
      (clojure.string/split #" ")
      rest
      (#(clojure.string/join " " %))
      clojure.string/trim))

(defn message
  [s]
  (->> (clojure.string/split s #" ")
       rest
       (clojure.string/join " ")
       clojure.string/trim))

(defn message
  [s]
  (-> s
      (clojure.string/split #" ")
      (->> rest
           (clojure.string/join " ")
           clojure.string/trim)))

(defn message
  [s]
  (-> s
      (clojure.string/split #" ")
      (->> rest
           (clojure.string/join " ")
           clojure.string/trim)))

(defn message
  [s]
  (as-> s $
    (clojure.string/split $ #" ")
    (rest $)
    (clojure.string/join " " $)
    (clojure.string/trim $)))

(defn message-as
  [s]
  (-> s
      (clojure.string/split #" ")
      rest
      (as-> another
            (clojure.string/join " " another))
      (clojure.string/trim)))

#_(message-as "hello world and everyone else!")


;; aoc 2022 day 3

(defn find-badge [lines]
  (first (apply set/intersection (map set lines))))

(defn char-priority
  [c]
  ;; _ because they want a = 1
  (str/index-of "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" c))

(def data
  (-> (slurp "./resources/aoc/2022day3sample.txt")
      str/split-lines))

(->> data
     (map (fn [line] (split-at (/ (count line) 2) line)))
     #_(map #(split-at (/ (count %) 2) %))
     (map find-badge)
     (map char-priority)
     (apply +))

;; part 2

(->> data
     (partition 3)
     (map find-badge)
     (map char-priority)
     (apply +))

;; reimplementing partition

(defn our-partition-all [n coll]
  (loop [c coll
         acc []]
    (if (empty? c)
      acc
      (recur (drop n c) (conj acc (take n c))))))

(our-partition-all 3 [1 2 3 4 5 6 7])

(defn our-partition
  ([n coll]
   (our-partition n n coll))
  ([n step coll]
   (loop [c coll
          acc []]
     (if (< (count c) n)
       acc
       (recur (drop step c) (conj acc (take n c)))))))

(our-partition 3 1 [1 2 3 4 5 6 7 8 9])
(our-partition 3 [1 2 3 4 5 6 7 8 9])
