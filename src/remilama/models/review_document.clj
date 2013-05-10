(ns remilama.models.review-document
  (:use
    [korma.core :exclude (insert update) :as korma]
    [remilama.models.base]))

(defentity review_documents)
(defcrud review_documents)

;; Local Variables:
;; eval: (rename-buffer "models/review_document.clj")
;; end:
