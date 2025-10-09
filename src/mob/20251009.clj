(ns mob.20251009
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [com.rpl.specter :as x]))

;; specter - walk
;; clojure.core/postwalk

;; problem: want to change all keys in a nested datastructure
;; from PascalCase to kebab-case

(def input
  {:ClusterArn
   "arn:aws:elasticmapreduce:us-east-1:000000000000:cluster/j-2VW43DKHTZYAX",
   :Id "j-2VW43DKHTZYAX",
   :Name "Some Test Cluster Name",
   :NormalizedInstanceHours 136,
   :Status {:State "TERMINATED",
            :StateChangeReason {:Code "ALL_STEPS_COMPLETED",
                                :Message "Steps completed"},
            :Timeline {:CreationDateTime #inst "2025-09-12T11:44:44.000-00:00",
                       :EndDateTime #inst "2025-09-12T11:56:08.000-00:00",
                       :ReadyDateTime #inst "2025-09-12T11:49:30.000-00:00"}}})

#_(x/select (x/walker keyword?) input)

;; 3 strategies for pascal->kebab
;;    - replace all uppercase with -lower, then remove first character
;;    * replace all uppercase (except first) with -lower, then lowercase first character
;;    - match all lowerUpper pairs and nothingUpper, replace conditionally

(defn pascal->kebab [s]
  ;; using a "negative lookbehind"
  (-> (string/replace s #"(?<!^)[A-Z]" #(str "-" %))
      (string/lower-case)))

#_(pascal->kebab "HelloWorldAll") ;; hello-world

;; 3 solutions to the original problem

;; specter
#_(x/transform [(x/walker keyword?) x/NAME] pascal->kebab input)

;; overdoing it with specter (digging into chars)
#_(->> input
       (x/transform [(x/walker keyword?)
                     x/NAME
                     (x/regex-nav #"(?<!^)[A-Z]")]
                    #(str "-" %))
       (x/transform [(x/walker keyword?)
                     x/NAME]
                    string/lower-case))

;; clojure.walk/postwalk
#_(walk/postwalk (fn [node]
                   (if (keyword? node)
                     (-> node
                         name
                         pascal->kebab
                         keyword)
                     node))
                 input)

;; new exercise: use postwalk to manually solve a formula:

(def formula '(plus 1 2 (multiply 3 5) (multiply 5 (divide 10 12))))

(walk/postwalk (fn [node]
                 (if (list? node)
                   (apply (case (first node)
                            plus +
                            multiply *
                            divide /)
                          (rest node))
                   node))
               formula)

(walk/postwalk (fn [node]
                 (if (list? node)
                   (let [result (apply (case (first node)
                                         plus +
                                         multiply *
                                         divide /)
                                       (rest node))]
                     (println node "=>" result)
                     result)
                   node))
               formula)

(def formula2 '(+ 1 2 (* 3 5) (* 5 (/ 10 12))))

(walk/postwalk (fn [node]
                 (if (list? node)
                   (apply (resolve (first node))
                          (rest node))
                   node))
               formula2)