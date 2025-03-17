(ns mob.20241121
  (:import
   [java.security MessageDigest]
   [java.math BigInteger])
  (:require
   [clojure.string :as str]))

;; Advent of Code - ?

(defn md5 [s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

#_(md5 "abc3231929")
#_(md5 "abc5017308")
#_(md5 "abc5278568")

#_(.digest algorithm (.getBytes "abc3231929"))

(def door-id "abc")

(def door-id "ffykfhsq")
;; part 1 - c6697b55
;; part 2 - 8c35d1ab

(comment
  ;; part 1
  (->> (range)
       (map #(str door-id %))
       (map md5)
       (keep #(second (re-matches #"00000(.).+" %)))
       (take 8)
       (reduce str)
       time)
  ;; ~70s

  ;; part 1 - trying to optimize
  (->> (range)
       (map #(str door-id %))
       (map md5)
       (filter #(str/starts-with? % "00000"))
       (take 8)
       (map #(nth % 5))
       (reduce str)
       time)

  ;; part 2
  (->> (range)
       (map #(str door-id %))
       (map md5)
       #_["000002cc"
          "asdasdasd"
          "000000aa"
          "000001bb"
          "000003bb"
          "0000044bb"
          "0000055bb"
          "0000066bb"
          "0000077bb"
          "0000066bb"
          "0000086bb"
          "0000096bb"
          "000001dd"]
       (keep #(next (re-matches #"00000([0-7])(.).+" %)))
       (reduce (fn [acc [pos char]]
                 (if (= 8 (count acc))
                   (reduced acc)
                   (if (get acc pos)
                     acc
                     (assoc acc pos char))))
               {})
       (sort-by first)
       (map second)
       (apply str)
       time)
  ;; ~300s
  )

;; homework - optimize - probably by avoiding working with strings
