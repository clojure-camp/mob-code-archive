(ns mob.20250814
  (:require [cljfx.api :as fx]))

;; some more playing with cljfx

;; multiple boxes, that have width, height, x, y
;;   draw them
;; connections between boxes
;;   draw the lines

;; need some javafx type that will let us place things "absolutely" 
;; (at a specific x,y)
;;    => :pane

(def rectangles
  [{:x 100
    :y 100
    :fill "red"
    :width 120
    :height 120
    :id 1}
   {:x 150
    :y 300
    :fill "green"
    :width 120
    :height 120
    :id 2}
   {:x 300
    :y 400
    :fill "blue"
    :width 120
    :height 120
    :id 3}])

(def connections
  [[1 2]
   [1 3]
   [2 3]])

(defn ret-rectangle [id]
  (some #(when (= id (:id %)) %) rectangles))

#_(ret-rectangle 2)

;; could store rectangles as {id rectangle}
;; would make ret-recangle faster and simpler

(def state (atom {}))

(defn root [{:keys [board]}]
  {:fx/type :stage
   :showing true
   :title "title"
   :width 500
   :height 500
   :scene
   {:fx/type :scene
    :root {:fx/type :pane
           :children (concat
                      (for [[from-id to-id] connections
                            :let [{start-x :x
                                   start-y :y
                                   start-width :width
                                   start-height :height} (ret-rectangle from-id)
                                  {end-x :x
                                   end-y :y
                                   end-width :width
                                   end-height :height} (ret-rectangle to-id)]]
                        {:fx/type :path
                         :elements (concat [{:fx/type :move-to
                                             :x (+ start-x (/ start-width
                                                              2))
                                             :y (+ start-y (/ start-height 2))}]
                                           (shuffle
                                            [{:fx/type :v-line-to
                                              :y (+ end-y (/ end-height 2))}
                                             {:fx/type :h-line-to
                                              :x (+ end-x (/ end-width 2))}]))})
                      (for [rectangle rectangles]
                        {:fx/type :image-view
                         :image {:fx/type :image
                                 :url "file:logo.png"
                                 :smooth true}
                         :fit-width (:width rectangle)
                         :fit-height (:height rectangle)
                         :x (:x rectangle)
                         :y (:y rectangle)}))}}})

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc assoc :fx/type root)))

(fx/mount-renderer state renderer)

