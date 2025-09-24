(ns mob.20250924
  (:require
   [datascript.core :as d]))

;; how to model different kinds of relationships in datomic?
;; example: a website where players can play various games with each other

;; entities:

;; BoardGame (Ex. Settlers, Chess)
;; GameSession (ex. Bob and Alice play Chess)
;; User (ex. Bob)

;; GameSession *- BoardGame
;; GameSession *-* User
;; User - Profile


;; ONE TO MANY

;; GameSession *- BoardGame

;; v1, :game-session/board-game
{:db/ident :game-session/board-game
 :db/valueType :db.type/ref
 :db/cardinality :db.cardinality/one}

[566 :board-game/name "Settlers"]
[123 :game-session/at #inst "2025-01-01"]
[124 :game-session/at #inst "2025-06-01"]
; relationships:
[123 :game-session/board-game 566]
[124 :game-session/board-game 566]

;; v2 :board-game/game-session

{:db/ident :board-game/game-session ;; idiomatically: not game-sessions
 :db/valueType :db.type/ref
 :db/cardinality :db.cardinality/many}

[566 :board-game/name "Settlers"]
[123 :game-session/at #inst "2025-01-01"]
[124 :game-session/at #inst "2025-06-01"]
; relationships:
[566 :board-game/game-sessions 123]
[566 :board-game/game-session 124]

;; for 1 to many, it is preferred to add the attribute to the side with cardinality 1
;; so that the single cardinality can be enforced (and pull returns a single thing not an array)
;; 
;; also, often in 1:many, want to cascade delete when deleting the "one"
;; for that, the rel needs to be on the one
;; (need to put :db/isComponent on the rel attribute in the schema)


;; MANY to MANY

;; GameSession *-* User

[123 :game-session/at #inst "2025-01-01"]
[124 :game-session/at #inst "2025-06-01"]
[998 :user/name "Alice"]
[999 :user/name "Bob"]

;; v1

{:db/ident :game-session/user ;; idiomatically: not users
 :db/valueType :db.type/ref
 :db/cardinality :db.cardinality/many}

[123 :game-session/user 998]
[123 :game-session/user 999]
[124 :game-session/user 999]

;; v2

{:db/ident :user/game-session ;; idiomatically: not users
 :db/valueType :db.type/ref
 :db/cardinality :db.cardinality/many}

[998 :user/game-session 123]
[999 :user/game-session 123]
[999 :user/game-session 124]

;; which side? doesn't really matter
;;
;; unless, there is a parent-child relationship where you would want to  
;; retract all children when the parent is retracted
;; if so, then you want the relation on the parent side
;; (need to put :db/isComponent on the rel attribute in the schema)
;;
;; the other decision factor would be... which side do you want to have the
;;  attribute included in "by default" when doing a (pull 123 [*])


;; PUTTING IT INTO PRACTISE

(let [schema {:board-game/name
              {#_#_:db/valueType :db.type/string}

              :user/name
              {#_#_:db/valueType :db.type/string}

              :game-session/user
              {:db/valueType :db.type/ref
               :db/cardinality :db.cardinality/many}

              :game-session/at
              {#_#_:db/valueType :db.type/instant}

              :game-session/board-game
              {:db/valueType :db.type/ref
               :db/cardinality :db.cardinality/one}}
      conn (d/create-conn schema)]
  
  (d/transact! conn [{:db/id -5
                      :board-game/name "Catan"}
                     {;; don't need db/id if creating a new entity
                      :user/name "Alice"}])

  (d/transact! conn [;; note, the placeholder ids don't 
                     ;; maintain meaning across transactions
                     [:db/add -5 :board-game/name "Chess"]
                     [:db/add -1 :user/name "Bob"]])
  
  (let [bob-id (d/q '[:find ?e .
                      :where [?e :user/name "Bob"]]
                    @conn)
        catan-id (d/q '[:find ?e .
                        :where [?e :board-game/name "Catan"]]
                      @conn)
        alice-id (d/q '[:find ?e .
                        :where [?e :user/name "Alice"]]
                      @conn)]
    
    (d/transact! conn [[:db/add -8 :game-session/at #inst "2025"]
                       [:db/add -8 :game-session/user bob-id]
                       [:db/add -8 :game-session/user alice-id]
                       [:db/add -8 :game-session/board-game catan-id]])
    
    (d/transact! conn [{:game-session/at #inst "2025"
                        :game-session/user [alice-id bob-id]
                        :game-session/board-game catan-id}])

    (d/transact! conn [[:db/add -2 :board-game/name "Eclipse"]
                       {:db/id -5
                        :user/name "Cathy"}
                       {:game-session/at #inst "2026"
                        :game-session/user [alice-id -5 {:user/name "Dan"}]
                        :game-session/board-game -2}]))

  ;; all EAVs
  (d/q '[:find ?e ?a ?v
         :where [?e ?a ?v]]
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
       @conn))

;; homework:
;;   what are all the games that bob has played?
;;   who has played catan?
;;   what are all the players that bob has played with?
