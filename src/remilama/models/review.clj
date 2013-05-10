(ns remilama.models.review
  (:use
    [korma.core :exclude (insert update) :as korma]
    [remilama.models.base]))

(defentity reviews)
(defcrud reviews)

;; Local Variables:
;; eval: (rename-buffer "models/reviews.clj")
;; end:
