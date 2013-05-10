(ns remilama.models.base
  (:use [korma.core :exclude (insert update) :as korma]))

(defn defcrud [ent]
  (intern *ns* 'find
    (fn [id]
      (first (select ent
               (where {:id id}))))) 
  (intern *ns* 'find-all
    (fn [& conds]
      (select ent)))
     
  (intern *ns* 'insert
    (fn [attributes]
      (korma/insert ent
        (values attributes))))

  (intern *ns* 'update
    (fn [attributes]
      (korma/update ent
        (set-fields attributes)
        (where {:id (attributes :id)})))))


;; Local Variables:
;; eval: (rename-buffer "models/base.clj")
;; end:
