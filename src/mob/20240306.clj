(ns mob.20240306 
  (:require [clojure.string :as str]))

;; vigenere cipher

;; encrypt

(defn index [c]
  (- (int c) (int \A)))
  
{\A 0
 \B 1}

#_(.indexOf [\A \B \C] \A)

(char 67)

(> (+ (int \Z) (index \A))
   (int \Z))

(defn offset-char [c offset-c]
  (char (+ (int \A)
           (mod (+ (index c)
                   (index offset-c))
                (inc (index \Z))))))

#_(offset-char \A \B) ;; B
#_(offset-char \Z \D) ;; C


(mod 29 26)

(defn encrypt [message secret]
  (apply str (map offset-char message (cycle secret))))

(defn onset-char [c onset-c]
  (char (+ (int \A)
           (mod (- (index c)
                   (index onset-c))
                (inc (index \Z))))))

(defn decrypt [message secret]
  (apply str (map onset-char message (cycle secret))))


(defn offset-char-2 [f c onset-c]
  (char (+ (int \A)
           (mod (f (index c)
                   (index onset-c))
                (inc (index \Z))))))

(defn decrypt [message secret]
  (apply str (map (partial offset-char-2 -) 
                  message 
                  (cycle secret))))

(defn encrypt [message secret]
  (apply str (map (partial offset-char-2 +)
                  message 
                  (cycle secret))))

#_(onset-char \G \B)

#_(char->index \Z)
;; 0

#_(encrypt "GGGG" "ABC")
;; "GHIG"

#_(encrypt "ABCDE" "A")
;; "ABCDE"

#_(encrypt "AUSTIN" "TEST")
;; TYKMBR

#_(decrypt "TYKMBR" "TEST")


"CSASTPKVSIQUTGQUCSASTPIUAQJBSIQUTG"

(defn find-indexes [needle list]
  (reduce (fn [memo [i p]]
            (if (= p needle)
              (conj memo i)
              memo))
          []
          (map-indexed vector list)))


(defn find-indexes [needle list]
  (->> (map-indexed vector list)
       (filter (fn [[_ itm]] (= needle itm)))
       (map first)))

(defn find-indexes [needle list]
  (->> (map-indexed vector list)
       (keep (fn [[index itm]] 
               (when (= needle itm)
                 index)))))
      
(defn find-indexes [needle list]
  (->> list
       (keep-indexed
        (fn [index itm]
          (when (= needle itm)
            index)))))

#_(find-indexes 1 [1 2 5 1 6 2 5 1 6 2])


#_(map-indexed vector "ABCDEF")

#_(map-indexed identity "ABCDEF")

(map vector (range) "ABCDEF")

(map + 
     (range) 
     [0 1 2 3 4 5])

(take 3 (range))

{"CSASTP" [16 16]
 "C" [1 6 9 12]}
(let [message "CSASTPKVSIQUTGQUCSASTPIUAQJBSIQUTG"]
  (for [x (range 2 (/ (count message) 2))]
    (let [partitions (partition x 1 message)
          matches (->> partitions
                       frequencies
                       (filter #(> (val %) 1))
                       (map key))]
      (->> matches
           (map (fn [m]
                  [m
                   (find-indexes m partitions)]))
           #_(map (partial apply -))))))
         
  
  
  

str
(map #(str/index-of message %))