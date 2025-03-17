(ns mob.20240207)

;; draw down

;; max_value_so_far = 0
;; out_array = []
;; for loop

;; like above, w/ atoms
;; use map

;; reduce

;; recursion

;; loop - recur

;; reductions

(defn draw-down 
  [input]
  (let [max-value-so-far (atom 0)
        output (atom [])]
    (doall
     (map (fn [x]
            (when (< @max-value-so-far x)
              (reset! max-value-so-far x))
            (swap! output conj (/ x @max-value-so-far)))
          input))
    @output))

(defn draw-down
  [input]
  (->> input
       (reduce (fn [{:keys [max-value-so-far _output] :as acc} x]
                 (if (< x max-value-so-far)
                   (update acc :output conj (/ x max-value-so-far))
                   (-> acc
                       (assoc :max-value-so-far x)
                       (update :output conj 1))))
               {:max-value-so-far 0
                :output []})
       :output))

  
#_(draw-down [1 2 3 4 5 6])
;; [1.0 1.0 1.0 1.0 1.0 1.0]

#_(draw-down [2 2 2 1])
;; [1.0 1.0 1.0 0.5]

#_(draw-down [4 1 2 5])
;; [1.0 0.25 0.5 1.0]

