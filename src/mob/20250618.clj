(ns mob.20250618
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [honey.sql :as hsql]
   [honey.sql.helpers :as hsqlh]
   [hugsql.core :as hugsql]))

;; connecting to sqlite
;;    with:
;;     next.jdbc
;;     next.jdbc + honeysql
;;     hugsql
;; (worth also considering h2db, which is like sqlite, but java)

(def db {:dbtype "sqlite" 
         :dbname "clojureCamp.sqlite"})

#_(.delete (clojure.java.io/file "clojureCamp.sqlite"))

(def ds (jdbc/get-datasource db))

;; this is a macro that defines a bunch of functions
(hugsql/def-db-fns (clojure.java.io/file "src/mob/20250618.sql"))

(comment
  ;; creating table
  (jdbc/execute! ds
                 ["create table contact (
                     id integer primary key autoincrement,
                     name varchar(32),
                     email varchar(255))"])

  ;; inserting
  (jdbc/execute! ds ["insert into contact (name, email) values ('tim', 'tim@example.com')"])
  (jdbc/execute! ds ["insert into contact (name, email) values (?, ?)", "raf", "raf@example.com"])
  (sql/insert! ds "contact" {:name "Thomas"
                             :email "thomas@example.com"})
  (-> (hsqlh/insert-into :contact)
      (hsqlh/values [{:name "Greg" :email "gbb@example.com"}])
      (hsql/format)
      (->> (jdbc/execute! ds)))
  (jdbc/execute! ds (hsql/format {:insert-into [:contact]
                                  :values [{:name "Greg", :email "gbb@example.com"}]}))
  (insert-contact db {:name "raf" :email "raf@example.com"})

  ;; querying
  (jdbc/execute! ds ["select * from contact where name=?" "tim"])
  (sql/query ds ["select * from contact where name=?" "tim"])
  (sql/find-by-keys ds "contact" {:name "tim"})
  (jdbc/execute! ds (hsql/format {:select :*
                                  :from   [:contact]
                                  :where  [:= :contact.name "tim"]}))
  (all-contacts db) 
  ;; note, hugsql isn't namespacing the results like next.jdbc
  ;; but it is possible to have it use next.jdbc and namespace the results:
  ;; https://www.hugsql.org/hugsql-adapters/next-jdbc-adapter

  (sql/find-by-keys ds "contact" :all)
  (sql/find-by-keys ds "contact" :all {:limit 2}))