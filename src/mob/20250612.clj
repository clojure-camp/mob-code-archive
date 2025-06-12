(ns mob.20250612
  (:require
   [hiccup.core :as h]))

;; manually drawing graphs with svgs

(defonce data
  (->> (range 0 40)
       (map (fn [i]
              {:country (gensym)
               :population (* i 100000 (rand-nth [0.95 1.25 1.1 0.98]) (/ 40))
               :gdp (* i 10000 (rand-nth [0.95 1.25 1.1 0.98]) (/ 40))
               :category (rand-nth [:a :b :c :d])
               :other (rand-int 1000)}))))

;; scatterplot
;; with population vs gdp
;; color of dot -> category
;; other -> size of the dot
;; data x: 0 to 100000
;; graph x: 0 to 1000 (with padding tweaks)

(defn graph []
  (let [pad 25
        max-x 100000
        max-y 10000
        max-size 1000
        width 600
        height 300
        ->x (fn [v]
              (- (/ v (/ max-x width)) (- pad)))
        ->y (fn [v]
              (- height (/ v (/ max-y height)) pad))
        ->size (fn [v]
                 (/ v (/ max-size 10)))]
    [:svg
     {:viewBox (str 0 " " 0 " " width " " height)
      :xmlns "http://www.w3.org/2000/svg"
      :style "background:yellow"}
     (for [{:keys [population gdp category other]} data]
       [:circle {:cx (->x population)
                 :cy (->y gdp)
                 :r (->size other)
                 :fill ({:a "green" :b "blue" :c "red" :d "cyan"} category)}])
     [:line {:x1 (->x 0) :y1 (->y 0) :x2 (->x 0) :y2 (->y max-y) :stroke "black" :stroke-width 1}]
     [:line {:x1 (->x 0) :y1 (->y 0) :x2 (->x max-x) :y2 (->y 0) :stroke "black" :stroke-width 1}]
     (for [x (range 0 100000 10000)]
       [:text {:font-size 10
               :text-anchor "middle"
               :x (->x x) 
               :y (+ 10 (->y 0))} x])
     (for [y (range 0 10000 2000)]
       [:text {:font-size 10
               :alignment-baseline "text-top"
               :text-anchor "end"
               :x pad :y (->y y)} y])]))


(spit "out.html" (h/html
                  [:html
                   [:head
                    [:meta {:http-equiv "refresh" :content 5}]]
                   [:body
                    (graph)]]))
