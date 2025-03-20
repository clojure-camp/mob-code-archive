(ns mob.20250320)

;; robot name generator

(defn make-unique-generator
  [init-set f]
  (let [prev-values (atom init-set)]
    (fn []
      (loop [attempts 0]
        (if (< attempts 10000)
          (let [o (f)]
            (if (contains? @prev-values o)
              (recur (inc attempts))
              (do (swap! prev-values conj o)
                  o)))
          (throw (ex-info "too many attempts" {})))))))

(def random-name (make-unique-generator #{}
                                        (fn []
                                          (repeatedly 10 #(rand-nth "abcdefghijklmnopqrstuvwxyz")))))

(random-name)

(def random-name-explody (make-unique-generator #{}
                                        (fn []
                                          (rand-nth "abc"))))

(random-name-explody)

;; bug, there's a race-condition!

;; thread a - checks
;; thread a - swaps
;; thread b - checks
;; thread b - swaps

;; thread a - checks
;; thread b - checks
;; thread a - swaps
;; thread b - swaps

;; rewrite with compare-and-set!

(defn make-unique-generator
  [init-set generator-fn]
  (let [prev-values (atom init-set)]
    (fn []
      (loop [attempts 0]
        (if (< attempts 10000)
          (let [id (generator-fn) 
                old-values @prev-values]
            (if (or (contains? old-values id) 
                    (not (compare-and-set! prev-values
                                           old-values
                                           (conj old-values id)))) 
              (recur (inc attempts))
              id))
          (throw (ex-info "too many attempts" {})))))))

;; refactor to be less confusing:

(defn make-unique-generator
  [init-set generator-fn]
  (let [prev-values (atom init-set)]
    (fn []
      (loop [attempts 0]
        (if (< attempts 10000)
          (let [id (generator-fn)
                old-values @prev-values]
            (cond (contains? old-values id)
                  (recur (inc attempts))

                  (compare-and-set! prev-values
                                    old-values
                                    (conj old-values id))
                  id

                  :else
                  (recur (inc attempts))))
          (throw (ex-info "too many attempts" {})))))))

;; don't waste the potentially valid id when hit the race-condition:

(defn make-unique-generator
  [init-set generator-fn]
  (let [prev-values (atom init-set)]
    (fn []
      (loop [attempts 0
             id (generator-fn)]
        (if (< attempts 10000)
          (let [old-values @prev-values]
            (cond (contains? old-values id)
                  (recur (inc attempts) (generator-fn))

                  (compare-and-set! prev-values
                                    old-values
                                    (conj old-values id))
                  id

                  :else  ;; handle potential race condition
                  (recur (inc attempts) id)))
          (throw (ex-info "too many attempts" {})))))))