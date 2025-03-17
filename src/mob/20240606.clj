(ns mob.20240606
  (:require 
   [cheshire.core :as json]))

;; h-index

;; max value x where at least x values are >= x

;; citations  - # of papers w/ at least that many citations

{0 5
 1 4
 2 3
 3 3 ;<-
 4 2
 5 2
 6 1}

;; plan:
;; iterate starting at max(numbers), work down
;;   when i > count of papers with citations <= i
;;     return the first one is succesful 

(defn count-greater [n coll]
  (count (filter (fn [x] (<= n x)) coll)))

#_(count-greater 4 [3 0 1 6 5])

(defn h-score [citation-counts]
  (loop [h (apply max citation-counts)]
    (if (<= h (count-greater h citation-counts))
      h
      (recur (- h 1)))))
  

#_(apply max [3 0 1 6 5])

(h-score [3 0 1 6 5]) ; 3
(h-score [1 3 1]) ; 1
(h-score [5 2 1 4 2]) ; 2
(h-score [0 0 0 0 0]) ; 0

;; homework: reimplement  h-score using:
;; frequencies, sort, working from largest, accumulate, stop at first one where succesfull

;; part 2

;; given the json data, return list of authors ranked by h-score

(def papers 
  (-> "citations.json"
      (slurp)
      (json/parse-string true)))

papers

(->>
 #_[{:title "Q", :authors ["Alice Smith"], :citation_count 7}
    {:title "A", :authors ["Bob Johnson" "Alice Smith"], :citation_count 3}
    {:title "T", :authors ["Charlie Brown" "Alice Smith" "David Wilson"], :citation_count 10}]
 papers
 (mapcat (fn [paper]
           (map (fn [author]
                  [author (:citation_count paper)])
                (:authors paper))))
 #_[["Alice Smith" 7]
    ["Bob Johnson" 3]
    ["Alice Smith" 3]]
 (reduce (fn [acc [author cnt]]
           #_(update acc author (fn [list] (conj list cnt)))
           #_(update acc author #(conj % cnt))
           (update acc author conj cnt))
         {})
 #_{"Alice Smith" [7 3 10]
    "David Wilson" [10]
    "Bob Johnson" [3]}
 (map (fn [[author citation-counts]]
        [author (h-score citation-counts)]))
 #_[["Alice Smith" 5] ["David Wislon" 4] ...]
 (sort-by second #_(fn [[author h]] h) >))
