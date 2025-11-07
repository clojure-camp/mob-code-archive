(ns mob.20251106
  (:require
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; priority queue from scratch

#_(let [pq (priority-queue-with-fn :foo)]
    (-> pq
        (pq-conj {:foo 2 :bar :a})
        (pq-conj {:foo 1 :bar :b})
        (pq-conj {:foo 3 :bar :c})
        (pq-conj {:foo 2 :bar :d}))
    pq
    ;; '({:foo 1 :bar :b} {:foo 2 :bar :a} {:foo 2 :bar :d} {:foo 3 :bar :c})
    )

(sorted-map 1 :a 2 :b 3 :c 3 :d)

;; implementation
;;    lean on sorted-map
;;    store the comparator-fn in meta
;;
;; later: make it work w/ clojure's conj, (ie. implement the interfaces)

(defn priority-queue-with-fn [f]
  (with-meta (sorted-map) {:f f}))

#_{1 {:foo 1}
   2 {:foo 2}}

#_{1 [{:foo 1}]
   2 [{:foo 2} {:foo 2}]}

(meta (priority-queue-with-fn :test))

(defn pq-conj [q item]
  (update q ((-> q meta :f) item) (fnil conj []) item))

(defn pq-seq [pq]
  (mapcat val pq))

(defn remove-from-vector [v item]
  (let [index-to-remove (.indexOf v item)]
    (if (neg? index-to-remove)
      v
      (vec (concat (subvec v 0 index-to-remove)
                   (subvec v (inc index-to-remove)))))))
  
(defn remove-from-vector [coll val-to-remove]
  (let [[head tail] (split-with #(not= % val-to-remove) coll)]
    (vec (concat head 
                 (rest tail)))))

(defn pq-disj [q item]
  (update q ((-> q meta :f) item) remove-from-vector item))

(rcf/tests
 "Basic"
 (-> (priority-queue-with-fn :foo)
     (pq-conj {:foo 2 :bar :a})
     (pq-conj {:foo 1 :bar :b})   
     (pq-conj {:foo 3 :bar :c})
     pq-seq)
 := '({:foo 1, :bar :b} 
      {:foo 2, :bar :a} 
      {:foo 3, :bar :c})

 "W/ Duplicate Priorities"
 (-> (priority-queue-with-fn :foo)
     (pq-conj {:foo 2 :bar :a})
     (pq-conj {:foo 1 :bar :b})
     (pq-conj {:foo 3 :bar :c})
     (pq-conj {:foo 2 :bar :d})
     pq-seq)
 := 
 '({:foo 1 :bar :b} 
   {:foo 2 :bar :a} 
   {:foo 2 :bar :d} 
   {:foo 3 :bar :c})
 
 "W/ Duplicate Values"
 (-> (priority-queue-with-fn :foo)
     (pq-conj {:foo 2 :bar :a})
     (pq-conj {:foo 1 :bar :b})
     (pq-conj {:foo 3 :bar :c})
     (pq-conj {:foo 2 :bar :a})
     pq-seq)
 :=
 '({:foo 1 :bar :b} 
   {:foo 2 :bar :a} 
   {:foo 2 :bar :a} 
   {:foo 3 :bar :c})
 
 "Disj - ignores"
 (-> (priority-queue-with-fn (constantly 0))
     (pq-conj :a)
     (pq-disj :b)
     pq-seq)
 := '(:a)

 "Disj - simple"
 (-> (priority-queue-with-fn :foo)
     (pq-conj {:foo 2 :bar :a})
     (pq-conj {:foo 1 :bar :b})
     (pq-conj {:foo 3 :bar :c})
     (pq-disj {:foo 1 :bar :b})
     pq-seq)
 := '({:foo 2, :bar :a}
      {:foo 3, :bar :c})
 
 "W/ Duplicate Priorities"
 (-> (priority-queue-with-fn :foo)
     (pq-conj {:foo 2 :bar :a})
     (pq-conj {:foo 1 :bar :b})
     (pq-conj {:foo 3 :bar :c})
     (pq-conj {:foo 2 :bar :d})
     (pq-disj {:foo 2 :bar :d})
     pq-seq)
 :=
 '({:foo 1 :bar :b}
   {:foo 2 :bar :a}
   {:foo 3 :bar :c})
 
 "W/ Duplicate Values"
 (-> (priority-queue-with-fn :foo)
     (pq-conj {:foo 2 :bar :a})
     (pq-conj {:foo 1 :bar :b})
     (pq-conj {:foo 3 :bar :c})
     (pq-conj {:foo 2 :bar :a})
     (pq-disj {:foo 2 :bar :a})
     pq-seq)
 :=
 '({:foo 1 :bar :b}
   {:foo 2 :bar :a}
   {:foo 3 :bar :c}))

