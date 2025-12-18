(ns mob.20251217
  (:require
   [hiccup.core :as h]))

;; SVG christmas card

(defn tree [{:keys [width height x y stack-percent]}]
  (let [tip-x x
        tip-y y
        left-x (- x (* 0.5 width))
        left-y (+ y height)
        right-x (+ x (* 0.5 width))
        right-y left-y]
  [:polygon {:points (str tip-x "," tip-y " "
                          left-x "," left-y " "
                          right-x "," right-y)
             :fill (str "lch(" (+ 10 (* stack-percent 20)) "% 22 150)")}]))

(def svg
  (let [width 800
        height 600]
    [:svg {:style {:background "pink"
                   :border "1px solid black"}
           :width width
           :height height}

     ;; sky
     [:rect {:width width
             :height height
             :fill "lightblue"}]

     ;; ground
     (let [ground-height (/ height 4)]
       [:rect {:y (- height ground-height)
               :width width
               :height ground-height
               :fill "white"}])

     ;; trees
     (for [z (range 20)]
       (tree {:width (+ 100 (rand 10))
              :height (+ 180 (rand 50))
              :x (rand width)
              :y (+ 260 (* z (rand 10)))
              :stack-percent (/ z 20)}))

     ;; snow
     (for [_ (range 500)]
       [:circle {:cx (rand width)
                 :cy (rand height)
                 :r (+ (rand 3) 2)
                 :fill "white"}])

     ;; text
     [:text {:fill "red"
             :text-anchor "middle"
             :x (/ width 2)
             :y (/ height 3)
             :font-size 100
             :font-family "Kaushan Script"}
      "Happy Holidays"]
     [:text {:fill "red"
             :text-anchor "middle"
             :x (/ width 2)
             :y (+ 60 (/ height 3))
             :font-size 50
             :font-family "Kaushan Script"}
      "from Clojure Camp"]]))

(def html
  (h/html
   [:html
    [:head
     [:link {:href "https://fonts.googleapis.com/css2?family=Kaushan+Script&display=swap" :rel "stylesheet"}]]
    [:body
     svg]]))

(spit "xmas.html" html)

