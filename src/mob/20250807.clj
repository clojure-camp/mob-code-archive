(ns mob.20250807 
  (:require
   [clojure.string :as str]))

;; reduce, in theory, possible, but akward
;; so, we will attempt loop first (maybe try while if we have time)

(defn run-program [prog]
  (loop [prog prog
         index 0]
    (cond
      (= (prog index) 1)
      (recur (assoc prog
                    (prog (+ index 3))
                    (+ (prog (prog (+ index 1)))
                       (prog (prog (+ index 2)))))
             (+ index 4))

      (= (prog index) 2)
      (recur (assoc prog
                    (prog (+ index 3))
                    (* (prog (prog (+ index 1)))
                       (prog (prog (+ index 2)))))
             (+ index 4))

      (= (prog index) 99)
      prog)))

;; could refactor the above to avoid the duplication
;; (could even use multimethods?)

;; part 1

(defn part-1 []
  (-> (slurp "resources/aoc/2019day2.txt")
      (str/split #",")
      (->> (mapv parse-long))
      (assoc 1 12 2 2)
      (run-program)
      (get 0)))

(part-1)

;; part 2

;; for (for generating all combinations of 0..99 and 0..99)
;; map (for running all the programs)
;; some (? for getting the first one that matches)

(defn part-2 []
  (let [input (-> (slurp "resources/aoc/2019day2.txt")
                  (str/split #",")
                  (->> (mapv parse-long)))
        candidates (for [noun (range 100)
                         verb (range 100)]
                     [noun verb (-> input
                                    (assoc 1 noun 2 verb)
                                    (run-program)
                                    (get 0))])]
    (some (fn [[noun verb result]]
            (when (= 19690720 result)
              (+ (* noun 100)
                 verb)))
          candidates)))

(part-2)