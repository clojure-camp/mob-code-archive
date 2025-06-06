(ns mob.20250605
  (:require [cljfx.api :as fx]))

;; building tic-tac-toe w/ clj-fx
;; https://github.com/cljfx/cljfx

(def state (atom {:player "x"
                  :board [["" "x" ""]
                          ["" "" ""]
                          ["" "" "o"]]}))


(defn root [{:keys [board]}]
  {:fx/type :stage
   :showing true
   :title "title"
   :width 500
   :height 500
   :scene
   {:fx/type :scene
    :root {:fx/type :v-box
           :children
           (for [[row-index row] (map-indexed vector board)]
             {:fx/type :h-box
              :children
              (for [[cell-index cell] (map-indexed vector row)]
                {:fx/type :button
                 :text cell
                 :on-action
                 (fn [_]
                   (when (= cell "")
                    (swap! state (fn [state]
                                   (-> state
                                       (assoc-in [:board row-index cell-index] (:player state))
                                       (update :player {"x" "o"
                                                        "o" "x"}))))))})})}}})

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc assoc :fx/type root)))

(fx/mount-renderer state renderer)

;; TODO
;; use keywords and nil instead of strings in our state
;; reset button
;; determining the winner
;; split cell into its own component?
;; create a play! fn

