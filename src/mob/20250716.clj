(ns mob.20250716)

;; find (and/or verify) the best strategy for "the secretary problem"
;; https://en.wikipedia.org/wiki/Secretary_problem

;; potential strategies to implement and test:
;;  ex.
;;    just choose the X%th one
;;    after interviewing X%, choose the next one that is higher than all that we've seen
;;    choose the one that beats N previous ones

;; design

;; how do we reprsent the candidates?
;;
;; [ 1 2 3 ...] shuffled


;; how do we represent a strategy?
;;
;; a) strategy(candidates-array) => returns the index  (strategy can cheat)
;; b) construct-strategy(n)
;;      => strategy(next-candidate) => true, false   (would need to keep state internally)
;; c) strategy(memo, next-candidate) => memo, + true or false    (runner would need to maintain state)
;;      similar to reduce

;; choose (c), most functional

;; how to simulate and compare a strategy? (~ monte carlo)
;;
;; generate a whole of bunch inputs (random order)
;; generate our strategy
;; call our strategy on each input
;; have a fn to score the strategy result
;; collect all result together
;; fn to calculate an average score

(defn strategy-choose10th
  [memo next-candidate _candidate-count commit]
  (if (nil? memo)
    1
    (if (< memo (dec 10))
      (inc memo)
      (commit next-candidate))))

(defn strategy-choose10%
  [memo next-candidate candidate-count commit]
  (if (nil? memo)
    1
    (if (< (/ memo candidate-count) 1/10)
      (inc memo)
      (commit next-candidate))))

(defn strategy-see10%-then-pick-next-highest
  [memo next-candidate candidate-count commit]
  (cond
    (nil? memo)
    {:count 1
     :best next-candidate}

    (:count memo)
    (if (< (/ (:count memo) candidate-count) 0.37)
      (-> memo
          (update :count inc)
          (update :best (fn [current]
                          (if (< current next-candidate)
                            next-candidate
                            current))))
      {:threshold (:best memo)})

    ;; all below in "threshold phase"

    (< (:threshold memo) next-candidate)
    (commit next-candidate)

    :else
    memo))

(defn average [col]
  (/ (apply + col) (count col)))

#_(average [1 2 3 4 101])

(defn run-strategy-broken [strategy]
  (let [n 1000
        candidates (shuffle (range 1 (+ n 1)))
        result (reduce (fn [memo candidate]
                         (strategy memo candidate n reduced))
                       nil
                       candidates)]
    ;; reduced? doesn't work, because reduce unwraps it
    ;; have to "reimplement" our own wrapper below
    (if (reduced? result)
      result
      (last candidates))))

(defn run-strategy [strategy]
  (let [n 100
        candidates (shuffle (range 1 (+ n 1)))
        result (reduce (fn [memo candidate]
                         (let [result (strategy memo candidate n (fn [x]
                                                                   {::commit x}))]
                           (if (::commit result)
                             (reduced result)
                             result)))
                       nil
                       (butlast candidates))]
    (or (::commit result)
        (last candidates))))

(defn evaluate-strategy [strategy]
  (double (average
           (repeatedly 1000
                       #(run-strategy strategy)))))

#_(evaluate-strategy strategy-choose10th)
#_(evaluate-strategy strategy-choose10%)
#_(evaluate-strategy strategy-see10%-then-pick-next-highest)

#_(run-strategy strategy-see10%-then-pick-next-highest)

;; homework:
;;    try implementing the other strategies
;;    consider refactoring to one of the other design choices (above)
