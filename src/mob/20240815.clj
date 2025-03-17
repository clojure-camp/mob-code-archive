(ns mob.20240815
  (:require 
   [clojure.string :as string]))

;; aoc 2018 day 4

(defn total-sleep-minutes [times]
  (->> times
       (partition 2)
       (map (fn [[sleep-start sleep-end]]
              (- sleep-end sleep-start)))
       (reduce +)))

(total-sleep-minutes [10 12 45 58])


(let [data (-> (->> (slurp "resources/aoc/2018day4sample.txt")
                    string/split-lines
                    sort
                    (map (fn [line]
                           ;; TODO maybe parse it nicer here, then having to do hacky parsing later
                           (rest (string/split line #":|(] )|#"))))
                    doall
                    (reduce (fn [memo item]
                              (case (second item)
                                "Guard "
                                (let [new-guard-id (parse-long (first (string/split (last item) #" ")))]
                                  (-> memo
                                      (assoc :last-guard new-guard-id)
                                      (update new-guard-id (fn [prev-value]
                                                             (if (nil? prev-value)
                                                               []
                                                               prev-value)))))

                                "falls asleep"
                                (update memo
                                        (get memo :last-guard)
                                        (fn [times]
                                          (conj times (parse-long (first item)))))
                                "wakes up"
                                (update memo
                                        (get memo :last-guard)
                                        (fn [times]
                                          (conj times (parse-long (first item)))))))
                            {:last-guard nil}))
               (dissoc :last-guard))]
                
  (update-vals data total-sleep-minutes))


(comment 

 [10 12 45 58] => 2 + 13 => 15

 [10 12 45 58] => [[10 12] [45 58]] => [2 13] => 15


 {
  {10 []
   :last-guard 10}
  {10 [asleep-minute]
   :last-guard 10}

  {10 [asleep-minute awake-minute]
   :last-guard 10}

  {10 [asleep-minute wake-minute asleep-minute wake-minute]
   11 [asleep-minute wake-minute asleep-minute wake-minute]}

  {10 24
   11 59}

  {10 [[asleep-time wake-time]
       [asleep-time wake-time]]
   11 []}

;; from that, get guard that slept most

  {10 [[asleep-time wake-time]
       [asleep-time wake-time]]}

;; from that, get minute they slept most

  24})


