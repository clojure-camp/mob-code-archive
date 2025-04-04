(ns mob.20250403)

;; Solving the Monty Hall Problem with Monte Carlo simulation

;; Suppose you're on a game show, and you're given the choice of three doors: Behind one door is a car; behind the others, goats. You pick a door, say No. 1, and the host, who knows what's behind the doors, opens another door, say No. 3, which has a goat. He then says to you, "Do you want to pick door No. 2?" Is it to your advantage to switch your choice?

(def prizes [:win :lose :lose])

(defn choose [prizes]
  (rand-int (count prizes)))

(defn host-reveal [prizes choice]
  (ffirst (filter (fn [[k v]] (= :lose v)) (dissoc (into {} (map-indexed vector prizes)) choice))))

(defn host-reveal [prizes choice]
  (->> prizes
       (keep-indexed
         (fn [idx prize]
           (when (and (= prize :lose) (not= idx choice))
             idx))) 
       (rand-nth)))

(defn game [strategy]
  (let [game-prizes (shuffle prizes)
        value (fn [index] (nth game-prizes index))
        ;; contestants choice
        choice (choose game-prizes)
        ;; hosts choice
        revealed-door (host-reveal game-prizes choice)
        remaining-indices (remove #{choice revealed-door} (range (count game-prizes)))] 
    (case strategy
      :switch (value (rand-nth remaining-indices))
      :stay (value choice))))

#_(game :switch)

(defn play-games [times strategy]
  (repeatedly times #(game strategy)))

#_(play-games 5 :switch)

(defn simulate [n]
  (letfn [(per [strategy]
               (let [{:keys [win lose]} (frequencies (play-games n strategy))]
                 (double (/ win (+ win lose)))))]
    {:switch (per :switch)
     :stay (per :stay)}))

#_(simulate 5000)

;; why letfn? - usually just for mutual recursion

(letfn [(even?' [x] (if (zero? x) true (odd?' (dec x))))
        (odd?' [x] (if (zero? x) false (even?' (dec x))))]
  (even?' 4))

(let [even?' (fn [x] (if (zero? x) true (odd?' (dec x))))
      odd?' (fn [x] (if (zero? x) false (even?' (dec x))))]
  (even?' 4))

;; set a seed so we can test our functions

(let [r (java.util.Random. 2000)]
  (with-redefs [clojure.core/shuffle (fn [coll]
                                       (let [al (java.util.ArrayList. coll)]
                                         (java.util.Collections/shuffle al r)
                                         (clojure.lang.RT/vector (.toArray al))))
                clojure.core/rand-int (fn [n]
                                        (int (* (.nextDouble r) n)))
                clojure.core/rand-nth (fn [coll]
                                        (nth coll (int (* (.nextDouble r) (count coll)))))]
    (simulate 5000)))