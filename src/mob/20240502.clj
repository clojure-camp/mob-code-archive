(ns mob.20240502
  (:require
   [clojure.set :as set]
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

(defn score [target guess]
  (let [correct-count (atom 0)
        incorrect-location-count (atom 0)
        guess-colors (atom (set guess))]
    (mapv (fn [target-item guess-item]
            (cond
              (= target-item guess-item)
              (swap! correct-count inc)

              (contains? (set target) guess-item)
              (when (contains? @guess-colors guess-item)
                (swap! incorrect-location-count inc)
                (swap! guess-colors disj guess-item))))
          target
          guess)
    {:correct @correct-count
     :incorrect-location @incorrect-location-count
     :not-exists (- (count target) @correct-count @incorrect-location-count)}))


;; map-indexed, mapv, map (w/ 2 lists)
;; set, contains?

;; for each item in target
;;    return whether it is: correct, incorrect-location, not-exists
;; then, count each type of result
;; [:R :G :R :B]  ; target
;; [:R :B :B :R]  ; guess  1 corr, 2 incorr

;; [_ :G :R :B]
;; [_ :B :B :R]

(defn score [target guess]
  (let [correct (count (filter true? (map = target guess)))
        incorrect-location (let [x (remove (fn [[a b]] (= a b)) (map vector target guess))
                                 target-remaining (set (map first x))
                                 guess-remaining (set (map last x))]
                             (count (set/intersection target-remaining guess-remaining)))]
    {:correct correct
     :incorrect-location incorrect-location
     :not-exists (- (count target) correct incorrect-location)}))




(defn score [target guess]
  (let [correct (count (filter true? (map = target guess)))
        incorrect-location 
        (let [x (remove (fn [[a b]] (= a b)) (map vector target guess))
              target-remaining (map first x)
              guess-remaining (map last x)]
          (apply + (vals
                    (merge-with
                     (fn [target-count guess-count]
                       (min target-count guess-count))
                     (merge {:R 0 :G 0 :Y 0 :X 0 :B 0}
                            (frequencies target-remaining))
                     (merge {:R 0 :G 0 :Y 0 :X 0 :B 0}
                            (frequencies guess-remaining))))))]
    {:correct correct
     :incorrect-location incorrect-location
     :not-exists (- (count target) correct incorrect-location)}))

;; gpt 
(defn score [guess secret]
  (let [secret-freq (frequencies secret)
        guess-freq (frequencies guess)
        whites-and-blacks (reduce + (map #(min (get secret-freq % 0)
                                               (get guess-freq % 0)) (keys guess-freq)))
        blacks (count (filter true? (map = guess secret)))]
    {:correct blacks 
     :incorrect-location (- whites-and-blacks blacks)
     :not-exists (- (count guess) whites-and-blacks)}))

(defn scorex [target guess]
  (let [correct (count (filter true? (map = target guess)))
        incorrect-location
        (count (reduce (fn [acc item]
                         (when-not (contains? (set acc) item)
                           (conj acc item)))
                       []
                       (filter #(not= (first %) (second %)) (mapv (fn [m n]
                                                                    [m n]) target guess))))]
    {:correct correct
     :incorrect-location incorrect-location
     :not-exists (- (count target) correct incorrect-location)}))

#_(score [:R :R :B :B]
         [:R :R :R :B])

(remove (fn [[a b]] (= a b)) (map vector [1 3 4] [1 5 2]))

(map vector [1 3 4] [1 5 2])

(map (fn [a b]
       (vector a b)) [1 3 4] [1 5 2])

(vector 1 2)


(frequencies [:B :G :G :B :Y])
(frequencies [:R :B :B :X :G])
(frequencies [:B :G :G :B :Y :Y :Y])
(frequencies [:R :B :B :X :G :G :G])
;;  target [:B]
;;  guess  [:R] 




(rcf/tests
 (score [:B :G :G :B :Y :Y :Y :R]
        [:R :B :B :X :G :G :G :R])
 :=
 {:incorrect-location 4
  :correct 1
  :not-exists 3}

 (score [:B :G :G :B]
        [:R :B :B :X])
 :=
 {:incorrect-location 2
  :correct 0
  :not-exists 2}


 (score [:R :R :B :B]
        [:R :R :R :B])
 :=
 {:incorrect-location 0
  :correct 3
  :not-exists 1}
 
 (score [:R :R :G :G]
        [:G :R :B :B])
 :=
 {:incorrect-location 1
  :correct 1
  :not-exists 2}
 
 (score [:G :R :R :B]
        [:R :R :G :G])
 :=
 {:incorrect-location 2
  :correct 1
  :not-exists 1}
 
 (score [:G :R :G :B]
        [:R :X :X :R])
 :=
 {:incorrect-location 1
  :correct 0
  :not-exists 3}
 
 (score [:R :G :G :G]
        [:R :R :R :R])
 :=
 {:incorrect-location 0
  :correct  1
  :not-exists 3})


;
;; inputs

;;   guess
    ;; "RRGG"
;; target
    ;; "RRGG"
;; [\R \R \G \G]
;; ["R" "R" "G" "G"]
;; [:red :red :green :green]
;; [1 0 0 1]
;; [1 2 3


;; ;; outputs

;; "1 incorrect-location; 2 correct"
;; {:incorrect-location 2
;;  :correct 1
;;  :not-exists 1} ;; <--- maybe leave this out
;; [2 1]

; R R G G     ; <- target

; G B B B     ; one - incorrect-location;   three not exist

; R B B B     ; one - correct location;  three -not exist

; R G G B     ; two - correct;  one - incorrect location;  one - not exist
