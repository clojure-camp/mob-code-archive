(ns mob.20240801
  (:require
   [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

;; create account
;; deposit
;; withdraw
;; check balance
;; transfer amount between accounts
;; see transactions

(defn not-negative? [state]
  (every? (comp (complement neg?) :balance) state))

(def bank (atom [{:balance 0 :history []}]
                :validator not-negative?))

(defn create-account!
  []
  (swap! bank conj {:balance 0 :history []}))

(defn deposit!
  [account amount]
  (swap! bank (fn [s] (-> s
                          (update-in [account :balance] + amount)
                          (update-in [account :history] conj amount)))))

(defn withdraw!
  [account amount]
  (swap! bank (fn [s] (-> s
                          (update-in [account :balance] - amount)
                          (update-in [account :history] conj (- amount))))))

(defn transfer! [from to amount]
  ;; this is ok, because withdraw! will throw an exception, and deposit! will fail
  ;; (though, it isn't thread safe) - could do this in a single swap
  (withdraw! from amount)
  (deposit! to amount))

(defn balance
  [account]
  (get-in @bank [account :balance]))

(defn transactions
  [account]
  (get-in @bank [account :history]))

(rcf/tests

 (not-negative? [{:balance 0} {:balance 44}]) := true
 (not-negative? [{:balance 0} {:balance -1} {:balance 44}]) := false

 (deref bank) := [{:balance 0 :history []}]

 (create-account!)

 (deref bank) := [{:balance 0 :history []}
                  {:balance 0 :history []}]
 (let [account 0
       amount 50]
   (deposit! account amount)
   (balance account) := amount)

 (let [account 0
       amount 20]
   (withdraw! account amount)
   (balance account) := 30)

 (transfer! 0 1 1000) :throws java.lang.IllegalStateException

 (transfer! 0 1 12)
 (balance 0) := 18
 (balance 1) := 12

 (transactions 0) := [50 -20 -12]
 (transactions 1) := [12])

;; homework
;;
;; determine balance dynamically?
;; just have a single global transaction history
;; pass bank state around to each function, rather than referencing globally
;; implement transfer! thread-safely (a single swap!)
  
