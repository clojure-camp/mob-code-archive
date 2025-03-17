(ns mob.20221208)

;; inputs are "litres/s"
;; bucket is leaking 1 l/s
;; bucket has capacity of 10
;; does the input overflow the bucket?

(defn leaky-bucket-overflowing? 
  [limit list]
  (->> list
       (map dec)
       (reductions +)
       (some #(< limit %))
       boolean))
  
(defn leaky-bucket-overflowing?
  [limit list]
  (let [level (atom 0)
        overflowed? (atom false)]
    (doseq [entity list]
      (swap! level + entity)
      (swap! level dec)
      (when-not @overflowed?
        (reset! overflowed? (< limit @level))))
    @overflowed?))

(defn leaky-bucket-overflowing?
  [limit list]
  (loop [level 0
         items list]
    (cond 
      (< limit level)
      true
      (empty? items)
      false
      :else
      (recur (+ level (first items) -1) 
             (rest items)))))

#_(leaky-bucket-overflowing? 10 [12]) ;; true
#_(leaky-bucket-overflowing? 10 [1 1 1 1 1 1 1 1]) ;; false
#_(leaky-bucket-overflowing? 10 [10 0 0 0 0 0 3]) ;; false
#_(leaky-bucket-overflowing? 10 [12 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]) ;; true
#_(leaky-bucket-overflowing? 10 [2 0 0 0 0 0 0 2 1 3 1 0 0 0 1 1 1 0 1]) ;; false
