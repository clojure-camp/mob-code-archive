(ns mob.20260121)

;; input:

;; table (vector of vectors) ~CSV
;; each column has different type (int, strings, boolean)
;; "profiling configuration" / "rules"
;;   for each type, define certain calculations/rules/checks
;;     all
;;       count of null in column
;;     int
;;        min
;;        max
;;        average
;;     strs
;;        min-length
;;        max-length
;;        avg-length
;;        count-of-ascii-only
;;        set of characters used
;;     boolean
;;        count true
;;        count false

;; assume types with a column are consistent (or null)
;;   (can use first non-null value from a column to determine the type)

(comment
  (profile rules table)

  {:column-a {:type :int
              :null-count 3
              :min 5
              :max 1000
              :avg 6}}

  ;; design of rules

  ;; idea A

  [{:rule-id :null-count
    :type :int
    :fn (fn [values])}]

  (def null-count
    {:rule-id :null-count
     :type :int
     :fn (fn [values])})

  [null-count]

  ;; idea B

  {:any {}
   :int {:min (fn [])
         :max (fn [])
         :avg (fn [])}}

  ;; idea C  <--- choose, for educational purposes

  (defn null-count
    {:profile-type :int}
    [x])

  [null-count]
  )

;; --------------------


(def table 
  [[1    "aaa"   true]
   [2    "bbb"   nil]
   [nil  "ccc"   false]
   ])

(defn minimum-length
  {:profile-type java.lang.String}
  [values]
  ;; TODO
  )

(defn null-count
  {:profile-type java.lang.Long}
  [values]
  (count (filter nil? values)))

(null-count [1 2 nil])

(defn profile [rules rows]
  (let [column->type (fn [column]
                       ;; TODO: do it based on first non-null value
                       (type (first column)))
        type->rules (group-by
                     (fn [rule] (:profile-type (meta rule)))
                     rules) ;; {long [#'null-count #'minimum]}
        column->report (fn [column]
                         (let [column-type (column->type column)]
                           (->> (type->rules column-type)
                                (map (fn [rule] [rule (rule column)]))
                                (into {:type column-type}))))
        ;; "transpose"
        columns (apply map list rows) ;; [[1 2 nil] ["aaa" "bbb" "ccc"]]
        ]
    (map column->report columns)))

(profile [#'null-count #'minimum-length] table)

;; output:
[{:type java.lang.Long
  #'null-count 3
  #'minimum 2}]


;; homework: recreate with tablecloth
;; https://scicloj.github.io/tablecloth/#aggregate