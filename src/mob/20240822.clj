(ns mob.20240822)

;; need to track order of card

;; output: index of the player who won

;; how do we represent a card?

;; suits: hearts, diamonds, clubs, spades

;; 4 of hearts

"H4"
{:suit :hearts
 :rank 4} ;; ++
{:suit "H"
 :rank 4}
{:card/suit :suit/hearts
 :card/rank 4} ;; +
[:suit/hearts 4]
:hearts/rank4

(max 1 5 1 2 62)
(max [1 5 1 2 62])

(defn compare-cards [card-1 card-2 lead-suit trump-suit])
  

#_(compare-cards {:suit :hearts
                  :rank 5}
                 {:suit :spades
                  :rank 10}
                 :hearts
                 nil)

(->> '(2 1 6 5 4)
     (map-indexed vector)
     (apply max-key second)
     #_first)

(def trump-suit :diamonds)
(def cards [{:suit :hearts
             :rank 4}
            {:suit :hearts
             :rank 5}
            {:suit :spades
             :rank 10}
            {:suit :hearts
             :rank 1}])

(defn winner-old
  ([cards, trump-suit]
   (if (seq (filter #(= (:suit %) trump-suit) cards))
     (->> cards
          (map-indexed (fn [idx item] (assoc item :index idx)))
          (filter #(= (:suit %) trump-suit))
          (apply max-key :rank)
          :index)
     (winner-old cards)))
  ([cards]
   (let [lead-suit (-> cards first :suit)]
    (->> cards
         (map-indexed (fn [idx item] (assoc item :indesx idx)))
         (filter #(= (:suit %) lead-suit))
         (apply max-key :rank)
         :index))))

(defn winner
  [cards trump-suit]
  (let [lead-suit (-> cards first :suit)
        compare-suit (if (some #{trump-suit} (map :suit cards))
                       trump-suit 
                       lead-suit)]
    (->> cards
         (map-indexed (fn [idx item] (assoc item :index idx)))
         (filter #(= (:suit %) compare-suit))1
         (apply max-key :rank)
         :index)))

(some #{trump-suit} (map :suit cards))

#_(winner [{:suit :hearts
            :rank 4}
           {:suit :hearts
            :rank 5}
           {:suit :spades
            :rank 10}
           {:suit :hearts
            :rank 1}]
          nil)
;; 1

#_(winner [{:suit :hearts
            :rank 4}
           {:suit :hearts
            :rank 5}
           {:suit :spades
            :rank 10}
           {:suit :hearts
            :rank 1}]
          :diamonds)
;; 1

#_(winner [{:suit :hearts
            :rank 4}
           {:suit :hearts
            :rank 5}
           {:suit :spades
            :rank 2}
           {:suit :hearts
            :rank 1}]
          :spades)
  ;; 2

