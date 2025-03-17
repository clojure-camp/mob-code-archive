(ns mob.20211005)

(def planets
  {:earth 1
   :mars (+ 1 9/12)
   :saturn (+ 29 6/12)})

(defn convert-space-age
  "Converts age from one planet to another"
  [age from-planet-key to-planet-key]
  (double (* (from-planet-key planets)
             (/ age
                (to-planet-key planets)))))

#_(:earth planets)

#_(planets :earth)

#_(get planets :earth)

#_(convert-space-age 30 :earth :saturn)
#_(convert-space-age 30 :earth :earth)
