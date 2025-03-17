(ns mob.20220602
 (:require
   [clojure.string :as str]))

;; input: "Some arbitrary length text", 10 (characters)
;; output: "Some..."

(defn abbr [text limit]
  (let [abbr-sym "..."
        abbr-sym-length (count abbr-sym)
        words-list (str/split text #" ")]
   (loop [words (rest words-list)
          output (first words-list)]
    (let [word (first words)
          proposed-length (+ (count output) 1 (count word) abbr-sym-length)]
     (cond
       (nil? word)
       (str output abbr-sym)

       (= proposed-length limit)
       (str output " " word abbr-sym)

       (< limit proposed-length)
       (str output abbr-sym)

       :else
       (recur (rest words) (str output " " word)))))))


#_(abbr "Some arbitrary length text" 18)
#_(abbr "Some arbitrary length text" 1)
#_(abbr "Some arbitrary length text" 7)
