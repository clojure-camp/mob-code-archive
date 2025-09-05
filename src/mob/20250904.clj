(ns mob.20250904)

;; inputs
;;    gas price -> dollars/gallon
;;    distance -> miles
;;    vehicle -> miles/gallons    (/ 1 mpg)
;; output
;;    dollars

;; dollars/gallon * gallon/mile * miles

(def vehicle->mpg
  {:honda-civic   35
   :nissan-sentra 30})

#_(get vehicle->mpg :honda-civic)

(defn travel-cost [gas-price distance vehicle-name]
  (* gas-price
     distance
     ;; gpm of vehicle
     (/ (get vehicle->mpg vehicle-name))))

#_(travel-cost 5.05 100 :honda-civic)
#_(travel-cost 5.05 100 :nissan-sentra)



;; (ripped the next few fns from chatgpt)

(def earth-radius-km 6371.0)

(defn deg->rad [deg]
  (* deg (/ Math/PI 180.0)))

(defn haversine [?]
  (let [s (Math/sin (/ ? 2.0))]
    (* s s)))

(defn great-circle-distance
  "Computes the great-circle distance in kilometers between two points
   specified as [lat lon] in degrees."
  [[lat1 lon1] [lat2 lon2]]
  (let [lat1 (deg->rad lat1)
        lon1 (deg->rad lon1)
        lat2 (deg->rad lat2)
        lon2 (deg->rad lon2)
        dlat (- lat2 lat1)
        dlon (- lon2 lon1)
        a (+ (haversine dlat)
             (* (Math/cos lat1)
                (Math/cos lat2)
                (haversine dlon)))
        c (* 2 (Math/atan2 (Math/sqrt a) (Math/sqrt (- 1.0 a))))]
    (* earth-radius-km c)))

#_(great-circle-distance [43.651070 -79.347015] 
                         [33.667963 -84.01779])

;; back to our code...

(def places
  {:toronto {:lon -79.347015
             :lat 43.651070}
   :conyers {:lon -84.01779
             :lat 33.667963}
   :sacramento {:lon -121.49
                :lat 38.58}})

#_(get-in places [:toronto :lat])

(defn km->mi [km]
  (* km 0.62137119))

#_(km->mi 100)
#_(km->mi 200)

(defn travel-cost-v2 [args]
  (let [distance-km (great-circle-distance [(get-in places [(get args :from) :lat])
                                            (get-in places [(get args :from) :lon])]
                                           [(get-in places [(get args :to) :lat])
                                            (get-in places [(get args :to) :lon])])
        distance-miles (km->mi distance-km)]
    (* (get args :gas-price)
       distance-miles
       (/ (get vehicle->mpg
               (get args :vehicle))))))

#_(travel-cost-v2 {:gas-price 5.05
                   :vehicle :honda-civic
                   :from :toronto
                   :to :conyers})

#_(travel-cost-v2 {:gas-price 5.05
                   :vehicle :honda-civic
                   :from :toronto
                   :to :sacramento})

;; some refactorings...

;; 1 - remove let

(defn travel-cost-v2 [args]
  (* (get args :gas-price)
     (km->mi (great-circle-distance [(get-in places [(get args :from) :lat])
                                     (get-in places [(get args :from) :lon])]
                                    [(get-in places [(get args :to) :lat])
                                     (get-in places [(get args :to) :lon])]))
     (/ (get vehicle->mpg
             (get args :vehicle)))))

;; 2 - destructure args
;; https://clojure.org/guides/destructuring

(defn travel-cost-v2
  [{:keys [from to gas-price vehicle]}]
  (let [{lat1 :lat lon1 :lon} (places from)
        {lat2 :lat lon2 :lon} (places to)]
    (* gas-price
       (km->mi (great-circle-distance [lat1 lon1]
                                      [lat2 lon2]))
       (/ (get vehicle->mpg
               vehicle)))))

;; 3 - no need for get

(defn travel-cost-v2
  [{:keys [from to gas-price vehicle]}]
  (let [{lat1 :lat lon1 :lon} (places from)
        {lat2 :lat lon2 :lon} (places to)]
    (* gas-price
       (km->mi (great-circle-distance [lat1 lon1]
                                      [lat2 lon2]))
       (/ (vehicle->mpg vehicle)))))

#_(get {:a 1} :a)
#_({:a 1} :a)




