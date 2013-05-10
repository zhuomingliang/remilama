(ns remilama.controllers.review-comment
  (:use
    [compojure.core :only (defroutes GET POST DELETE PUT)]
    [hiccup.util :only (url)]
    [noir.validation])
  (:require
    [noir.response :as response]
    [cemerick.friend :as friend]
    [remilama.models.review-comment :as ReviewComment]))  

(defn list [params]
  (ReviewComment/find-all
    (select-keys [:review_id :document_id])))

(defn create [comment]
  (let
    [result
      (try
        (ReviewComment/insert
          (merge comment
            { :commented_at (new java.util.Date)
              :commented_by_id ((friend/current-authentication) :username)}))
        (catch Exception e {:result "failure"}))]
    (response/json result)))

(defroutes routes
  (GET  "/" {params :params} (list params))
  (POST "/" {params :params} (create params)))

;; Local Variables:
;; eval: (rename-buffer "controllers/review_comment.clj")
;; end:
