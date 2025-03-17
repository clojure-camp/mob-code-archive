(ns mob.20240516 
  (:require [clojure.string :as str]))

;; aoc 2016 day 6

(defn transpose [x]
  (apply map vector x))
  
(defn most-common [coll]
  (key
   (apply max-key
          val
          (frequencies coll))))

(defn most-common [coll]
  (->> coll
       ;; frequencies
       (reduce (fn [a i]
                (update a i (fnil inc 0)))
               {})
       ;; max-key
       (reduce (fn [[_ max-cnt :as a] [_ cnt :as new]]
                 (if (< max-cnt cnt)
                   new
                   a)))
       key))
                         

(defn most-common [coll]
  (reduce (fn [a i]
            (update a i #(if % (inc %) (inc 0))))
          {}
          coll))
    

(most-common [\e \d \e \r \a \t \s \r \n \n \s \t \v \v \d \e])
(most-common "39rh2093ghf2093ghwkehg")

;; part 1

(->> (str/split (slurp "resources/aoc/2016day6.txt") #"\n")
     transpose
     (map most-common)
     (apply str))

;; part 2

(defn least-common [coll]
  (key
   (apply min-key
          val
          (frequencies coll))))

(->> (str/split (slurp "resources/aoc/2016day6.txt") #"\n")
     transpose
     (map least-common)
     (apply str))
     
;; transpose then frequencies


(let [max-char (apply max (vals {\e 3, \d 2, \r 2, \a 1, \t 2, \s 2, \n 2, \v 2}))]
  (ffirst (filter (fn [[k v]] (= max-char v))
                  {\e 3, \d 2, \r 2, \a 1, \t 2, \s 2, \n 2, \v 2})))

(key (last (sort-by val {\e 3, \d 2, \r 2, \a 1, \t 2, \s 2, \n 2, \v 2})))


;; let's redo it "online"

(->> (str/split (slurp "resources/aoc/2016day6sample.txt") #"\n")
     (map (fn [row]
            (map (fn [char] {char 1}) row)))
     (reduce (fn [acc row]
               (map (fn [a b]
                      (merge-with + a b))
                    acc row)))
     (map (fn [cnts]
            (key
             (apply max-key
                    val
                    cnts)))))


(def result (atom '({\e 1} {\e 1} {\d 1} {\a 1} {\d 1} {\n 1})))

(defn next-step! [row]
  (swap! result
         (fn [acc]
           (map (fn [a b]
                  (merge-with + a b))
                acc row))))

(defn report []
  (map (fn [cnts]
        (key
         (apply max-key
                val
                cnts))) @result))

#_(deref result)

#_(report)

#_(next-step! '({\d 1} {\r 1} {\v 1} {\t 1} {\e 1} {\e 1}))
  
(doseq [row (->> (str/split (slurp "resources/aoc/2016day6sample.txt") #"\n")
                 (map (fn [row]
                        (map (fn [char] {char 1}) row)))
                 rest
                 (drop 5))]
  (next-step! row))
            
(reduce (fn [acc row]
          (map (fn [a b]
                 (merge-with + a b))
               acc row))
        ['({\e 1} {\e 1} {\d 1} {\a 1} {\d 1} {\n 1})
         '({\d 1} {\r 1} {\v 1} {\t 1} {\e 1} {\e 1})])