-- :name all-contacts :? :*
-- :doc Get all characters
select * from contact

-- :name insert-contact :!
-- :doc Insert one contact
insert into contact (name, email) values (:name, :email)