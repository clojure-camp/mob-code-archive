(ns mob.20260128 
  (:require
    [clojure.math :as math]
    [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; problem: "curling scoring"

;; at the end of a round, 2 different teams end up with 
;; several "stones" each near "the target"
;; the team with the closest stone near the target gets points
;; equal to the number of stones closer to the target that the other
;; player's stone closest to the target

;; ties: if there is a tie between two or more stones of both teams
;; those stones (and all subsequent stones) are not counted


;; DESIGN

;; one list to represent all stones
;;  or two seperate lists?
;;
;; [x y] or {:x _ :y _}
;;
(comment
  (score {:stones [{:x 1 :y 2 :team :red}
                   {:x 4 :y 2 :team :blue}]
          :target {:x 0 :y 0}})
  ;; =>
  ;; winner, and points
  {:team :red
   :points 2}
  
  )

;; PLANNING

;; will want (distance p1 p2) fn
;; (closest-team target stones)
;; filtering? reducing? group-by-distance? sort, take

;; IMPLEMENTATION

(defn distance
  [{x1 :x y1 :y} {x2 :x y2 :y}]
  (Math/sqrt (+ (Math/pow (- x2 x1) 2)
                (Math/pow (- y2 y1) 2))))

#_(distance {:x 1 :y 2} {:x 0 :y 0})

(defn score 
  [target stones]
  (let [scoring-stones
        (->> stones
             (group-by (partial distance target))
             (sort-by first)
             ;; remove any tied stones (from seperate teams) and ones further away
             (take-while (fn [[_ stones]]
                           (= 1 (count (distinct (map :team stones))))))
             ;; partition-by with team splits into "runs" of the same team
             (partition-by (fn [[_ stones]]
                             ;; eliminated multi-stone ties is previous step
                             ;; so all stones are of the same team
                             (:team (first stones))))
             first
             (mapcat val))]
    (when (seq scoring-stones)
      {:team (:team (first scoring-stones))
       :score (count scoring-stones)})))


(rcf/tests
 (score {:x 0 :y 0}
        [;; this tie is ok
         {:x 1 :y 1 :team :red}
         {:x 1 :y 1 :team :red}
         {:x 5 :y 3 :team :red}
         ;; other team's stone, so rest don't count
         {:x 6 :y 8 :team :blue}
         {:x 8 :y 8 :team :red}])
 := {:team :red
     :score 3}

 (score {:x 0 :y 0}
        [{:x 1 :y 1 :team :red}
         {:x 3 :y 4 :team :red}
         {:x 4 :y 3 :team :red}
         ;; tied, so rest don't count
         {:x 6 :y 8 :team :red}
         {:x 6 :y 8 :team :blue}
         {:x 8 :y 8 :team :red}])
 := {:team :red
     :score 3}

 (score {:x 0 :y 0}
        [;; tied, so none count
         {:x 1 :y 1 :team :red}
         {:x 1 :y 1 :team :blue}
         {:x 4 :y 3 :team :red}])
 := nil



 nil)