(ns remilama.models.player
  (:use [korma.core :exclude (insert update) :as korma]))

(defentity players)

(defn find [id]
  (first (select players
    (where {:id id}))))

(defn find-all []
  (select players))

(defn insert [player]
  (korma/insert players
    (values player)))

(defn update [player]
  (korma/update players
    (set-fields player)
    (where {:id (player :id)})))

;; Local Variables:
;; eval: (rename-buffer "models/player.clj")
;; end:
