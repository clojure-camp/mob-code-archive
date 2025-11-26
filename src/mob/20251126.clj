(ns mob.20251126
  (:require
   [cheshire.core :as json]))

(def papers (json/decode (slurp "resources/citations.json")
                         keyword))

;; ===================================
;; CHALLENGE #1
;; which paper has most citations?

(last (sort-by :citation_count papers))

;; last is "expensive"
;; can pass a comparator to sort-by to "reverse" for free
;; so we can use first

(first (sort-by :citation_count > papers))

;; but sorting is O(nlogn), can do this in O(n)
;; let's use max-key:
;; (need apply because max-key expects multiple parameters, not a list)

(apply max-key :citation_count papers)

;; in case we didn't know max-key, let's use reduce:

(reduce (fn [acc i]
          (if (> (:citation_count i) 
                 (:citation_count acc))
            i
            acc))
        papers)

;; as a alternative, let's try loop:

(loop [best-so-far (first papers)
       col (rest papers)]
  (if (empty? col)
    best-so-far
    (if (< (:citation_count best-so-far) 
           (:citation_count (first col)))
      (recur (first col) (rest col))
      (recur best-so-far (rest col)))))

;; minor refactor (if returns a value, so can put it inside recur):

(loop [best-so-far (first papers)
       col (rest papers)]
  (if (empty? col)
    best-so-far
    (recur
     (if (< (:citation_count best-so-far)
            (:citation_count (first col)))
       (first col)
       best-so-far)
     (rest col))))

;; ===================================
;; CHALLENGE #2
;; what are all the unique author names?

(set (flatten (map :authors papers)))

;; same, but using ->>

(->> papers
     (map :authors)
     flatten
     set)

;; same, but using concat
;; (concat effectively flattens only one level, flatten is recursive)
;; (use concat b/c it's enough)
(->> papers
     (map :authors)
     (apply concat)
     set)

;; same, but use mapcat (map + concat)
(->> papers
     (mapcat :authors)
     set)

;; ===================================
;; CHALLENGE #3
;; how many papers per author?

(->> papers
     (mapcat :authors)
     frequencies)

;; ===================================
;; CHALLENGE #4
;; which author has most citations?

;; some brainstorming:

;; {:authors ["A" "B"] :citation_count 3} => {"A" 3 "B" 3} ;; w/ zipmap?
;; => [{"A" 3 "B" 3} {"A" 4 "C" 4}]
;; => merge-with

(->> [{"A" 3 "B" 3} {"A" 4 "C" 4}]
     (apply merge-with +))

(let [m {:authors ["A" "B"] :citation_count 3}]
  (zipmap (:authors m) ;; ["A" "B"]
          (repeat (:citation_count m)) ;; [3 3 3 3 3 3..]
          ))

;; authors and their citation counts:

(->> papers
     (map (fn [m]
            (zipmap (:authors m) 
                    (repeat (:citation_count m)))))
     (apply merge-with +))

;; author with most citations:

(->> papers
     (map (fn [m]
            (zipmap (:authors m)
                    (repeat (:citation_count m)))))
     (apply merge-with +)
     seq
     (apply max-key second)
     first)

;; same, but using key and val on MapEntry instead of first and second

(->> papers
     (map (fn [m]
            (zipmap (:authors m)
                    (repeat (:citation_count m)))))
     (apply merge-with +)
     ;; {"A" 3}
     (apply max-key val)
     key)

;; ===================================
;; CHALLENGE #5
;; which author pairs have written papers together? (how many)

;; (homework)