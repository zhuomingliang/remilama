(ns remilama.models.review-participant
  (:use
    [korma.core :exclude (insert update) :as korma]
    [remilama.models.base]))

(defentity review_participants)
(defcrud review_participants)

(defn delete-all [review-id]
  (korma/delete review_participants
    (where {:review_id review-id}))) 

;; Local Variables:
;; eval: (rename-buffer "models/participants.clj")
;; end:
