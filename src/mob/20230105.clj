(ns mob.20230105
  (:require
   [hyperfiddle.rcf :refer [tests]]))

; email input
; logic
;    -> needs-annotation
;        -> annotated
;    -> needs-attention
;        -> attended
;    -> refused
;    -> accepted


; state machine?  pre cond -> next state
; rules engines;  declare conditions & resulting transformations

(hyperfiddle.rcf/enable!)

(def sample-email
  {:email/subject "asdfasdf"
   :email/from "asdf"
   :email/to "asdf"
   :email/body ""
   :email/history [{:action/at #inst "2023-01-01"
                    :action/user "Bob"
                    :action/data [:set-foo! 1]}
                   {:action/at #inst "2023-01-01"
                    :action/user "Bob"
                    :action/data [:set-bar! 2]}]
   :email/annotations {:foo 1
                       :bar 2}
   :email/state :state/initial})

(def states
  #{:state/initial
    :state/needs-foo
    :state/needs-bar
    :state/refused
    :state/accepted})

(def actions
  [{:action/condition (fn [email]
                       (nil? (get-in email [:email/annotations :foo])))
    :action/effect (fn [email]
                    (assoc email :email/state :state/needs-foo))}])

(def sample-system
  {:system/emails [sample-email]
   :system/actions actions})
  

(defn apply-action
  [action email]
  ((:action/effect action) email))
  
(defn should-apply-action?
  [action email]
  ((:action/condition action) email))

(defn first-relevant-action
  [system email]
  (->> (:system/actions system)
       (filter (fn [action] (should-apply-action? action email)))
       first))
  
(defn process-email
  "Returns the email"
  [system email]
  (if-let [f (:action/effect (first-relevant-action system email))]
    (f email)
    email))
  
(defn run [system]
  (assoc system :system/emails (reduce (fn [emails email]
                                         (conj emails (process-email system email)))
                                       []
                                       (:system/emails system))))

;; for every email
;; check conditions, and apply the first relevant condition

(tests
 (should-apply-action?
  {:action/condition (fn [email]
                       (nil? (get-in email [:email/annotations :foo])))
   :action/effect (fn [email]
                    (assoc email :email/state :state/needs-foo))}
  {:email/history []
   :email/annotations {}
   :email/state :state/initial})
 :=
 true

 (apply-action
  {:action/condition (fn [email]
                       (nil? (get-in email [:email/annotations :foo])))
   :action/effect (fn [email]
                    (assoc email :email/state :state/needs-foo))}
  {:email/history []
   :email/annotations {}
   :email/state :state/initial})
 :=
 {:email/history []
  :email/annotations {}
  :email/state :state/needs-foo}
 
 (let [a {:action/condition (fn [email]
                              (nil? (get-in email [:email/annotations :foo])))
          :action/effect (fn [email]
                           (assoc email :email/state :state/needs-foo))}]
   (first-relevant-action
    {:system/emails [{:email/history []
                      :email/annotations {}
                      :email/state :state/initial}]
     :system/actions [a]}
    {:email/history []
     :email/annotations {}
     :email/state :state/initial})
   :=
   a)

 (process-email
  {:system/emails [{:email/history []
                    :email/annotations {}
                    :email/state :state/initial}]
   :system/actions [{:action/condition (fn [email]
                                         (nil? (get-in email [:email/annotations :foo])))
                     :action/effect (fn [email]
                                      (assoc email :email/state :state/needs-foo))}]}
  {:email/history []
   :email/annotations {}
   :email/state :state/initial})
 :=
 {:email/history []
  :email/annotations {}
  :email/state :state/needs-foo}

(let [a {:action/condition (fn [email]
                             (nil? (get-in email [:email/annotations :foo])))
         :action/effect (fn [email]
                          (assoc email :email/state :state/needs-foo))}]
 (run
  {:system/emails [{:email/history []
                    :email/annotations {}
                    :email/state :state/initial}]
   :system/actions [a]})
 :=
 {:system/emails [{:email/history []
                   :email/annotations {}
                   :email/state :state/needs-foo}]
  :system/actions [a]})
 
 
 nil)
 

