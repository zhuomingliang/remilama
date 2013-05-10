(ns remilama.models.document
  (:use
    [korma.core :exclude (insert update) :as korma]
    [remilama.models.base]))

(defentity documents)
(defcrud documents)

;; Local Variables:
;; eval: (rename-buffer "models/dcuments.clj")
;; end:
