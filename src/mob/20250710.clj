(ns mob.20250710)

;; challenge: biased random selection

;; what should our input be?

#_(biased-rand ?????)

{:foo 0.25
 :bar 0.5
 :baz 0.25}

{:foo 1/4
 :bar 1/2
 :baz 1/4}  ;; <--- this

{:foo 1
 :bar 2
 :baz 1}

[:foo :bar :baz]
[1/4 1/2 1/4]

#_(biased-rand {:foo 1/4 :bar 1/2 :baz 1/4}) ;; :foo


;; implemention ideas

;; use rand-nth, figure out how many of each to pass to rand-nth
;;    X - this will get very big or impossible with weird fraction combinations

;; use rand (0, 1) , divide up the range based on the input fractions
;;    pick this one

;;    foo               bar         baz
;; |----------|------------------|---------|
;; 0               r                       1


;; also: assert that inputs add up to 1

;; also: test that this is working correctly



;; v1 - generate the following list, then pick the first one where r < upper

;; [[:foo 0.25]
;;  [:bar 0.75]
;;  [:baz 1]]

(defn biased-rand
  [weights]
  {:pre [(= (apply + (vals weights)) 1)]}
  (let [r (rand)]
    (->> (reduce (fn [acc [k weight]]
                   (conj acc [k (+ weight (or (second (last acc)) 0))]))
                 []
                 weights)
         (filter (fn [[k upper]]
                   (< r upper)))
         (ffirst))))

;; v1b - instead of filter + fffirst, use some

(defn biased-rand
  [weights]
  {:pre [(= (apply + (vals weights)) 1)]}
  (let [r (rand)]
    (->> (reduce (fn [acc [k weight]]
                   (conj acc [k (+ weight (or (second (last acc)) 0))]))
                 []
                 weights)
         (some (fn [[k upper]]
                 (when (< r upper)
                   k))))))

;; v2 - use reduced

(defn biased-rand
  [weights]
  {:pre [(= (apply + (vals weights)) 1)]}
  (let [r (rand)]
    (reduce (fn [acc [k weight]]
              (let [upper-bound (+ weight (or (second (last acc)) 0))]
                (if (< r upper-bound)
                  (reduced k)
                  (conj acc [k upper-bound]))))
            []
            weights)))

;; v2b - simplify, don't need to accumulate k

(defn biased-rand
  [weights]
  {:pre [(= (apply + (vals weights)) 1)]}
  (let [r (rand)]
    (reduce (fn [acc [k weight]]
              (let [upper-bound (+ weight acc)]
                (if (< r upper-bound)
                  (reduced k)
                  upper-bound)))
            0
            weights)))

;; v3 - using loop

(defn biased-rand
  [weights]
  {:pre [(= (apply + (vals weights)) 1)]}
  (let [r (rand)]
    (loop [weights weights
           lower 0]
      #_(println "weights" weights "lower" lower)
      (let [[k weight] (first weights)
            upper (+ lower weight)]
        (if (< r upper)
          k
          (recur (rest weights)
                 upper))))))

(biased-rand {:foo 1/4 :bar 1/2 :baz 1/4})
#_(biased-rand {:foo 1/4 :bar 1/2 :baz 1/5}) ;; should fail pre assert

;; rough test:
#_(->> (repeatedly #(biased-rand {:foo 1/4 :bar 1/2 :baz 1/4}))
       (take 10000)
       frequencies)