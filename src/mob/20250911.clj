(ns mob.20250911
  (:require
   [clojure.string :as str]
   [com.rpl.specter :as x]))

;; https://github.com/redplanetlabs/specter

;; specter is like get-in, assoc-in, update-in - on steroids
;;    at every step of a path, instead of just one target, can have "navigators" that return and act on multiple
;;    similar to css selectors, x-path, lenses

;; get-in        getval
;; assoc-in      setval
;; update-in     transform

;; differences:
;;   the object of interest goes last
;;   maintains the same type (doesn't convert vectors to lists)

(def state {:users [{:email "alice@example.com"
                     :confirmed? true}
                    {:email "bob@example.com"
                     :confirmed? false}]})

;; TASK: get first user's email

(get-in state [:users 0 :email])
(x/select [:users 0 :email] state)  ;; note, returns a vector of emails; could use select-one

;; TASK: overwrite 

(assoc-in state [:users 0 :email] "new@example.com")
(x/setval [:users 0 :email] "new@example.com" state)

(update-in state [:users 0 :email] str/capitalize)
(x/transform [:users 0 :email] str/capitalize state)

;; so far, seems the same
;; but let's go deeper
;; a navigator can return 0+ results (rather than just 0 or 1 with get-in/assoc-in/update-in)

;; TASK return all emails:

(for [user (:users state)] (:email user))

(x/select [:users x/ALL :email] state)


;; TASK capitalize all emails

(update state :users (fn [users] (mapv #(update % :email str/capitalize) users)))

(x/transform [:users x/ALL :email] str/capitalize state)


;; TASK: return all unconfirmed emails

(->> (:users state)
     (remove :confirmed?)
     (map :email))
;; if you use a fn a navigator, specter treats it like a predicate (to filter with)

;; unconfirmed:
(x/select [:users x/ALL #(not (:confirmed? %))  :email] state)
(x/select [:users x/ALL (complement :confirmed?) :email] state)

;; confirmed:
(x/select [:users x/ALL #(:confirmed? %) :email] state)
(x/select [:users x/ALL (x/pred :confirmed?) :email] state)


;; TASK: capitalize all unconfirmed emails

(update state :users (fn [users]
                       (mapv (fn [user]
                               (if (:confirmed? user)
                                 user
                                 (update user :email str/capitalize))) 
                             users)))

(x/transform [:users x/ALL (complement :confirmed?) :email] str/capitalize state)

;; lots of other navigators: https://github.com/redplanetlabs/specter/wiki/List-of-Navigators

;; also has idea of "collecting" while navigating
;; "storing" some information mid-way through a navigation to make use of later

;; TASK: assoc the index of each user to the user

(x/select
 [:users
  x/INDEXED-VALS ;; this gets us [0 {...}]
  (x/collect x/FIRST) ;; we "store" the 0
  x/LAST ;; just nav to the user value
  ] 
 state)

(x/transform
 [:users
  x/INDEXED-VALS ;; this gets us [0 {...}]
  (x/collect x/FIRST) ;; we "store" the 0
  x/LAST ;; just nav to the user value
  ]
 (fn [[index] user]
   (assoc user :index index))
 state)

(x/transform
 [:users
  x/INDEXED-VALS ;; this gets us [0 {...}]
  (x/collect x/FIRST) ;; we "store" the 0
  x/LAST ;; just nav to the user value
  :index ;; can just navigate to the non-existing value
  ]
 (fn [[index] _value]
   index)
 state)

;; turns out, we didn't need to collect in this case:

(x/select
 [:users
  x/INDEXED-VALS ;; this gets us [0 {...}]
  ]
 state)

(x/transform
 [:users
  x/INDEXED-VALS ;; this gets us [0 {...}]
  ]
 (fn [[index user]]
   [index (assoc user :index index)])
 state)