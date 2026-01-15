(ns mob.20260115)

;; the problem:

;; input:
[
 {:id :foo
 :provides #{:x :y}}
 {:id :bar
 :provides #{:x :a}}
 {:id :baz
  :provides #{:a :b}}
]

;; return the "provides" keys that are provided more than once
;; and which ids provided them

;; output:
{:x #{:foo :bar}
 :a #{:bar :baz}}


;; plan 1:

[{:id :foo
  :provides #{:x :y}}
 {:id :bar
  :provides #{:x :a}}
 {:id :baz
  :provides #{:a :b}}]
;; =>mapcat=>
[[:foo :x]
 [:foo :y]
 [:bar :x]
 ,,,
 ]
;;=>reduce=>
{:x #{:foo :bar}
 :a #{:bar :baz}
 :y #{:foo}
 :b #{:baz}}
;; =>filter=>
{:x #{:foo :bar}
 :a #{:bar :baz}}



;; plan 2 
;; (replace the middle part of plan 1 as follows)
;; ...
;;=> mapcat =>
[{:x :foo}
 {:y :foo}
 {:x :bar}
 ,,,
 ]
;;=>merge-with=>
{:x #{:foo :bar}
 :a #{:bar :baz}
 :y :foo
 :b :baz}
;;=>filter out the singles=>
;; ...


;; v1 implementation

(->> [{:id :foo
       :provides #{:x :y}}
      {:id :bar
       :provides #{:x :a}}
      {:id :baz
       :provides #{:a :b}}]
     (mapcat (fn [{:keys [id provides]}]
               (map (fn [p] [p id]) provides)))
     #_[[:x :foo]
        [:y :foo]
        [:x :bar]
        ...]
     (reduce (fn [output [k id]]
               (update output k (fnil conj #{}) id))
             {})
     #_{:x #{:foo :bar}
        :a #{:bar :baz}
        :y #{:foo}
        :b #{:baz}}
     (filter (fn [[_ ids]] (< 1 (count ids))))
     (into {}))
#_{:x #{:foo :bar}
   :a #{:bar :baz}}


;; v2 implementation


(->> [{:id :foo
       :provides #{:x :y}}
      {:id :bar
       :provides #{:x :a}}
      {:id :baz
       :provides #{:a :b}}]
     (mapcat (fn [{:keys [id provides]}]
               (map (fn [p] {p #{id}}) provides)))
     #_[{:x #{:foo}}
        {:y #{:foo}}
        {:x #{:bar}}
        ,,,]
     (apply merge-with clojure.set/union)
     #_{:x #{:foo :bar}
        :a #{:bar :baz}
        :y #{:foo}
        :b #{:baz}}
     (filter (fn [[_ ids]] (< 1 (count ids))))
     (into {}))
#_{:x #{:foo :bar}
   :a #{:bar :baz}}


;; v2b

(defn invert-provides [coll]
  (->> (for [{:keys [id provides]} coll
             p provides]
         {p #{id}})
       (apply merge-with into)
       (filter (fn [[_ ids]] (< 1 (count ids))))
       (into {})))



;; v3 implemntation (group-by)

(->> [{:id :foo
       :provides #{:x :y}}
      {:id :bar
       :provides #{:x :a}}
      {:id :baz
       :provides #{:a :b}}]
     (mapcat (fn [{:keys [id provides]}]
               (map (fn [p] [p id]) provides)))
     #_[[:x :foo]
        [:y :foo]
        [:x :bar]
        ...]
     (group-by first)
     #_{:x [[:x :foo] [:x :bar]]
        :y [[:y :foo]]}
     ((fn [m]
        (update-vals m (fn [coll]
                         (set (map (fn [[_ v]] v) coll))))))
     #_{:x #{:foo :bar}
        :a #{:bar :baz}
        :y #{:foo}
        :b #{:baz}}
     (filter (fn [[_ ids]] (< 1 (count ids))))
     (into {}))
#_{:x #{:foo :bar}
   :a #{:bar :baz}}


