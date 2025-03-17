(ns mob.20240403
  (:require 
   [org.httpkit.client :as http]
   [cheshire.core :as json]
   [clojure.set :as set]))

;; exploring open ai embeddings based search

;; distance - given two vectors, returns the euclidean between them

(defn square [x]
  (* x x))

(defn d [p q]
  (square (- p q)))

(defn distance [vec1 vec2]
  (->> (map d vec1 vec2)
       (reduce +)
       Math/sqrt))

#_(distance [0] [2])
;; 2

#_(distance [0 0] [2 2])
;; ~ 2.8

#_(distance [0 0 0] [1 1 1])
;; 1.73

;; sort by + take

[{:distance 1
  :vec [1 1 1]}]

(defn closest [base-vec n vecs]
  (->> (sort-by (fn [vec]
                  (distance base-vec vec)) vecs)
       (take n)))


#_(closest [0 0 0]
           2
           [[9 4 2]
            [1 1 1]
            [1 2 3]
            [11 2 4]])
;; [[1 1 1] [1 2 3]]

(defn embedding [text]
  (-> @(http/request {:method :post
                       :url "https://api.openai.com/v1/embeddings"
                       :headers {"Content-Type" "application/json"
                                 "Authorization" (str "Bearer "
                                                      (slurp "open-api-key"))}
                       :body (json/encode {:input text
                                           :model "text-embedding-3-small"})})
       :body
       (json/decode)
       (get-in ["data" 0 "embedding"])))

#_(embedding "test")

(defonce db (atom {}))

(defn index! [db text]
  (if (get @db text)
    db
   (swap! db assoc text (embedding text))))

#_(keys @db)

#_(index! db "cat")
#_(index! db "dog")
#_(index! db "rock")
#_(index! db "kitten")
#_(index! db "monkey")
#_(index! db "I went on a trip to the beach")
#_(index! db "vacation")

;; 1 - don't run embedding if already exist in index
;; 2 - index as part of search

(defn search [db text n]
  (index! db text)
  (let [matches (closest (get @db text)
                  n
                  (vals (dissoc @db text)))
        inverted-db (set/map-invert @db)]
    (map inverted-db matches)))
    
(defn search [db text n]
  (index! db text)
  (->> @db
       (sort-by (fn [[k vec]]
                  (distance (@db text) vec)))
       (take (inc n))
       (rest)
       (map first)))

#_(search db "vacation" 4)

#_(search db "hamster" 1)

#_(search db "Great Expectations" 1)

#_(search db "Hitchiker's Guide to the Galaxy" 1)

#_(search db "science fiction" 3)

#_(search db "comedy" 4)

#_(search db "comedy book" 4)

#_(search db "I took my dog to the beach" 2)

#_(search db (pr-str *siyr) 2)