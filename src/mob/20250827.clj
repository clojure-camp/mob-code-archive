(ns mob.20250827
  (:require
   [org.httpkit.server :as http]
   [clojure.string :as str]))

;; part 1 - exploring http-kit

(def name "Bob")

(defn goodbye [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "goodbye " name)})

(defn app [request]
  (cond
    (= (:uri request) "/goodbye")
    (goodbye request)
    :else
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str "hello " name)}))

(comment
  ;; start server
  (def server (http/run-server #'app {:port 8084}))
  ;; stop server
  (server))

;; part 2
;; advent of code 2020 day 2

;; python-esque pseudecode:

;; lines = input split by \n
;; counter = 0
;; for line in lines:
;;     if valid(line)
;;        counter += 1

;; clojure-esque pseudocode
;; lines
;;   map parse-line
;;   filter valid?
;;   count

;; "1-3 a: abcde\n1-3 b: cdefg\n2-9 c: ccccccccc"
;;    => str/split
;; ["1-3 a: abcde" "1-3 b: cdefg" "2-9 c: ccccccccc"]
;;    => map parse-line
;; [{:min 1 :max 3 :char \a :password "abcde"} ...]
;;    => map valid?
;; [true false true]
;;    => filter true?
;; [true true]
;;    => count
;; 3

(def input (str/split (slurp "resources/aoc/2020day2sample.txt") #"\n"))

(defn parse-line [s]
  (let [parts (str/split s #"[ :-]+")]
    {:min (parse-long (first parts))
     :max (parse-long (second parts))
     :letter (first (get parts 2))
     :password (last parts)}))

(defn parse-line [s]
  ;; same, but with destructuring
  (let [[min max letter password] (str/split s #"[ :-]+")]
    {:min (parse-long min)
     :max (parse-long max)
     :letter (first letter) ;; convert single char string to char, "a" => \a
     :password password}))

#_(parse-line (first input))

(defn valid? [data]
  (<= (:min data) 
      ((frequencies (:password data)) (:letter data) 0) 
      (:max data)))

;; same, but destructuring the arguments
(defn valid? [{:keys [min max password letter]}]
  (<= min
      ;; instead of frequencies, could implement our own count-of
      ((frequencies password) letter 0)
      max))

#_(valid? {:password "asdfasdf" :letter \a :min 1 :max 3})

(comment
  ;; solution
  (count (filter valid? (map parse-line input)))

  ;; same, with ->>
  (->> input
       (map parse-line)
       (filter valid?)
       count)
  )

;; ways of getting values out of a map
(comment
  (get {:a 1} :a) ;; 1
  (get {:a 1} :b) ;; nil
  (get {:a 1} :b :default) ;; :default
  (get nil :a) ;; nil

  ({:a 1} :a) ;; 1 
  ({:a 1} :b) ;; nil
  ({:a 1} :b :default) ;; :default
  (nil :a) ;; exception

  (:a {:a 1}) ;; 1 
  (:b {:a 1}) ;; nil
  (:b {:a 1} :default) ;; :default
  (:a nil) ;; nil
  )







