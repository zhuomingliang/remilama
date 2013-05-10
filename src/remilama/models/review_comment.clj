(ns remilama.models.review-comment
  (:use
    [korma.core :exclude (insert update) :as korma]
    [remilama.models.base]))

(defentity review_comments)
(defcrud review_comments)

;; Local Variables:
;; eval: (rename-buffer "models/review_comment.clj")
;; end:
