(ns mob.20240103
  (:require
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; model and implement tennis scoring (in a tennis game)

;; table
;; (0, x) where x anything: 'Love', x
;; 1: '15'
;; 2: '30'
;;
;; (3, x) where x...: 40
;; (x, x+1) where x>=3: 'Advantage player 2'
;; (x, x) where x>=3: 'Deuce'
;; _____ "winner player 2"6 
 
(def point->score {0 "Love"
                   1 "15"
                   2 "30"
                   3 "40"})

(defn translate-score
  [player-1-score player-2-score]
  (cond
    (and (point->score player-1-score) 
         (point->score player-2-score) 
         (not= 3 player-1-score player-2-score))
    (str (point->score player-1-score) " - " 
         (point->score player-2-score))
    
    (= player-1-score player-2-score)
    "Deuce"
    
    (= (Math/abs (- player-1-score player-2-score)) 1)
    (str "Advantage Player " (if (< player-1-score player-2-score)
                               "2"
                               "1"))
    
    :else
    (str "Winner Player " (if (< player-1-score player-2-score)
                            "2"
                            "1"))))
    
(defn translate-score
  [player-1-score player-2-score]
  (cond
    (and (point->score player-1-score)
         (point->score player-2-score)
         (not= 3 player-1-score player-2-score))
    (str (point->score player-1-score) " - "
         (point->score player-2-score))
 
    (= player-1-score player-2-score)
    "Deuce"
 
    :else
    (str (if (= (Math/abs (- player-1-score player-2-score)) 1)
           "Advantage"
           "Winner")
         " Player "
         (if (< player-1-score player-2-score)
           "2"
           "1"))))    


(rcf/tests
  (translate-score 0 0) := "Love - Love"
  (translate-score 0 1) := "Love - 15"
  (translate-score 1 1) := "15 - 15"
  (translate-score 2 2) := "30 - 30"
  (translate-score 2 3) := "30 - 40"
  (translate-score 0 3) := "Love - 40"
  (translate-score 1 3) := "15 - 40"
  (translate-score 4 1) := "Winner Player 1"
  (translate-score 0 4) := "Winner Player 2"
  (translate-score 1 4) := "Winner Player 2"
  (translate-score 3 3) := "Deuce"
  (translate-score 3 4) := "Advantage Player 2"
  (translate-score 4 4) := "Deuce"
  (translate-score 5 4) := "Advantage Player 1"
  (translate-score 6 4) := "Winner Player 1"
 nil)
 
 