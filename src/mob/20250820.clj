(ns mob.20250820
  (:require
   [clojure.string :as str]))

;; advent of code 2019 day 5
;; https://adventofcode.com/2019/day/5

;; still a work in progress

(defn ->opcode [num]
  (rem num 100))

#_(->opcode 1001)

#_(->opcode 1012)

(defn ->parameter-mode [num index]
  (-> num
      str
      reverse
      (nth (+ index 2) \0)
      {\0 :mode/position
       \1 :mode/immediate}))

#_(->parameter-mode 1001 0)
:mode/position

#_(->parameter-mode 1001 1)
:mode/immediate

#_(->parameter-mode 1001 2)
:mode/position

(def ^:dynamic *input-value* nil)

(def instructions
  [{:opcode 1
    :name :addition
    :parameter-count 3
    :force-position [false false true]
    :operation (fn [a b position]
                 {:store-at position
                  :store-value (+ a b)})}
   {:opcode 2
    :name :multiply
    :parameter-count 3
    :force-position [false false true]
    :operation (fn [a b position]
                 {:store-at position
                  :store-value (* a b)})}
   {:opcode 3
    :name :input
    :parameter-count 1
    :force-position [true]
    :operation (fn [position]
                 {:store-at position
                  :store-value *input-value*})}
   {:opcode 4
    :name :output
    :parameter-count 1
    :force-position [false]
    :operation (fn [value]
                 (println value)
                 {})}
   {:opcode 99
    :name :end
    :parameter-count 0
    :operation (fn []
                 {:end true})}])

(def ->instruction
  (zipmap (map :opcode instructions)
          instructions))

(defn run-program [prog]
  (loop [memory prog
         index 0]
    (let [foo (get memory index)
          {:keys [parameter-count operation force-position]}
          (->instruction (->opcode foo))
          values (->> (range parameter-count)
                      (map (fn [p-index]
                             (let [parameter (get memory (+ index 1 p-index))]
                               (if (get force-position p-index)
                                 parameter
                                 (case (->parameter-mode foo p-index)
                                   :mode/immediate
                                   parameter
                                   :mode/position
                                   (get memory parameter)))))))
          result (apply operation values)]

      (cond
        (:end result)
        memory

        (:store-at result)
        (recur
         (assoc memory (:store-at result) (:store-value result))
         (+ index 1 parameter-count))

        :else
        (recur
         memory
         (+ index 1 parameter-count))))))

(defn parse [s]
  (-> s
      (str/split #",")
      (->> (mapv parse-long))))

(defn with-input [v result]
  result)

#_(-> (slurp "resources/aoc/2019day2.txt")
    parse
    (assoc 1 12 2 2)
    (run-program)
    (get 0))

#_(run-program (parse (slurp "./resources/aoc/2019day5.txt")))

#_(with-input 7 (run-program (parse (slurp "./resources/aoc/2019day5.txt"))))