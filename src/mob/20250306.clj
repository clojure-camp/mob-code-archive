(ns mob.20250306
  (:require
   [criterium.core :as crit]))

;; benching some fns

(defn a []
  (let [alphabet (vec "ABCDEFGHIJKLMNOPQRSTUVWXYZ")]
    (nth alphabet 24)))

(defn b []
  (let [alphabet (mapv char (range (int \A) (inc (int \Z))))]
    (nth alphabet 24)))

#_(crit/quick-bench
   (a)) ;; 400ns

#_(crit/quick-bench
   (b)) ;; 2400ns

(defn random-string [n]
  (let [alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ"]
    (apply str (repeatedly n #(rand-nth alphabet)))))

(def n 100000)
(def s (random-string (inc n)))
(def sv (into [] s))

(nth s n) ;; 33 ns

(.charAt s n) ;; 7600 ns (w/ reflection); 13 ns (w/o reflection)

(def ss (seq s))

(nth ss n) ;; 1,000,000 ns

(def sv (into [] s))

(nth sv n) ;; 17ns 
(get sv n) ;; 18ns


#_(crit/quick-bench
 (.charAt ^String s n))

#_(crit/quick-bench
 (get sv n))


