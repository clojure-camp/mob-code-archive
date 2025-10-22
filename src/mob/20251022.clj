(ns mob.20251022
  (:require
   [datascript.core :as d]
   [clojure.string :as str]))

;; who's here?
;; raf
;; tim
;; thomas
;; voortuck (bill)

;; continuing from mob.20250924

;; user >-< game-sessions - board-game

(def schema
  {:board-game/name
   {}

   :user/name
   {}

   :user/email
   {:db/unique :db.unique/identity}

   :game-session/user
   {:db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}

   :game-session/at
   {}

   :game-session/board-game
   {:db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}})

(defonce conn (d/create-conn schema))

(defn init! []

  (d/transact! conn [{:db/id -5
                      :board-game/name "Catan"}
                     {;; don't need db/id if creating a new entity
                      :user/name "Alice"
                      :user/email "alice@example.com"}])

  (d/transact! conn [;; note, the placeholder ids don't 
                     ;; maintain meaning across transactions
                     [:db/add -5 :board-game/name "Chess"]
                     [:db/add -1 :user/name "Bob"]
                     [:db/add -1 :user/email "bob@example.com"]])

  (let [catan-id (d/q '[:find ?e .
                        :where [?e :board-game/name "Catan"]]
                      @conn)]

    (d/transact! conn [[:db/add -8 :game-session/at #inst "2025-04-01"]
                       ;; can use identity attributes in place of entity-ids
                       [:db/add -8 :game-session/user [:user/email "bob@example.com"]]
                       [:db/add -8 :game-session/user [:user/email "alice@example.com"]]
                       [:db/add -8 :game-session/board-game catan-id]])

    (d/transact! conn [{:game-session/at #inst "2025"
                        :game-session/user [[:user/email "bob@example.com"] 
                                            [:user/email "alice@example.com"]]
                        :game-session/board-game catan-id}])
    
     (d/transact! conn [{:game-session/at #inst "2026"
                        :game-session/user [[:user/email "bob@example.com"]
                                            [:user/email "alice@example.com"]]
                        :game-session/board-game catan-id}])

    ;; inserting new games and new users all in one transaction
    (d/transact! conn [[:db/add -2 :board-game/name "Eclipse"]
                       {:db/id -5
                        :user/name "Cathy"
                        :user/email "cathy@example.org"}
                       {:game-session/at #inst "2026"
                        :game-session/user [[:user/email "alice@example.com"]
                                            -5 
                                            {:user/name "Dan"
                                             :user/email "dan@example.org"}]
                        :game-session/board-game -2}])))

#_(init!)

(comment
  ;; all EAVs
  (d/q '[:find ?e ?a ?v
         :where [?e ?a ?v]]
       @conn)

  ;; all the user names
  (d/q '[:find ?v
         :where [_ :user/name ?v]]
       @conn)

  ;; user name of entity with email alice@example.com
  (d/q '[:find ?v
         :where
         [?e :user/email "alice@example.com"]
         [?e :user/name ?v]]
       @conn)

  ;; user email of entitys with emails at @example.com
  (d/q '[:find ?email
         :where
         [?e :user/email ?email]
         [(clojure.string/ends-with? ?email "@example.com")]]
       @conn)

  (defn extract-domain [email]
    (second (str/split email #"@")))

  (extract-domain "raf@mentor.com")

  ;; return the email domains of all users
  (d/q '[:find ?domain
         :where
         [_ :user/email ?email]
         ;; custom fns must be fully namespaced
         [(mob.20251022/extract-domain ?email) ?domain]]
       @conn)

  ;; return all the users and the list of games they've played
  ;; [["Alice" #{"Catan"}] ["Bob" #{"Chess" "Catan"}]] 
  (d/q '[:find ?user (distinct ?game)
         :where
         [?u :user/name ?user]
         [?s :game-session/user ?u]
         [?s :game-session/board-game ?g]
         [?g :board-game/name ?game]]
       @conn)

  ;; what are all the names of players that bob has played with?
  (d/q '[:find ?other-user-name
         :where
         [?bob :user/name "Bob"]
         [?session :game-session/user ?bob]
         [?session :game-session/user ?other-user]
         [?other-user :user/name ?other-user-name]
         [(not= ?other-user ?bob)]]
       @conn)

  ;; every session and related entities
  (d/q '[:find (pull ?session [*
                               {:game-session/board-game [*]}
                               {:game-session/user [*]}])
         :where
         [?session :game-session/board-game _]]
       @conn)

  ;; every user and their sessions
  ;; https://docs.datomic.com/query/query-pull.html#reverse-lookup
  (d/q '[:find (pull ?user [* {:game-session/_user [*]}])
         :where
         [?user :user/name _]]
       @conn)
  )