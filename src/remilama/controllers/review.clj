(ns remilama.controllers.review
  (:use
    [clojure.string :only (split)]
    [compojure.core :as compojure :only (defroutes GET POST)]
    [taoensso.tower :as tower :only (with-locale with-scope t style)]
    [hiccup.util :only (url)]
    [noir.validation]
    [korma.db :only (transaction)])
  (:require
    [ring.util.response :as response]
    [cemerick.friend :as friend]
    [remilama.views.review :as view]
    [remilama.models.review :as Review]
    [remilama.models.player :as Player]
    [remilama.models.document :as Document]
    [remilama.models.review-document :as ReviewDocument]
    [remilama.models.review-participant :as ReviewParticipant]))  

(defn index []
  (let [reviews (Review/find-all)]
    (view/list reviews)))

(defn show [id]
  (let [review (Review/find id)]
    (view/show review)))

(defn new []
  (let [ players   (Player/find-all)
         documents (Document/find-all)]
    (view/new-review
      { :begin_at 600 :end_at   720}
      players documents)))

(defn edit [id]
  (let [ review    (Review/find id)
         players   (Player/find-all)
         documents (Document/find-all)]
    (view/edit review players documents)))

(defn- save [review save-fn]
  "Insert or update review."
  (rule (has-value? (review :name))
          [:name (t :errors/has-value (t :labels/name))])
  (rule (has-value? (review :review_date))
          [:review_date "review date must have a value"])
  (rule (has-value? (review :begin_at))
          [:begin_at "review time must have a value"])
  
  (if (errors?)
    (view/new-review (merge review {:errors @*errors*}))
    (do
      (transaction
        (let [result
               (apply save-fn
                 [(-> review
                    (assoc :reviewee_id 1)
                    (dissoc review :review_time :participants :review_documents))])]

          (ReviewParticipant/delete-all (or (review :id) (result :GENERATED_KEY)))
          (for [player (review :participants)]
            (ReviewParticipant/insert 
              { :review_id (or (review :id) (result :GENERATED_KEY))
                :player_id 1 })))) ;; TODO player_id
      (response/redirect (url "/review/")))))

(defn create [params]
  (save params Review/insert))


(defn update [params]
  (let [review (Review/find (params :id))]
    (save (merge review params) Review/update)))

(defroutes routes
  (compojure/context "/review" request
    (GET "/" [] (index))
    (GET "/show/:id" [id] (show id))
    (GET "/new" [] (remilama.controllers.review/new))
    (GET "/edit/:id" [id] (edit id))
    (POST "/create" {params :params}
      (create
        (merge params
          (apply assoc {}
            (interleave [:begin_at :end_at]
              (map #(Integer. %)
                (split (params :review_time) #";")))))))
    (POST "/update/:id" {params :params}
      (println params)
      (update
        (merge params
          (apply assoc {}
            (interleave [:begin_at :end_at]
              (map #(Integer. %)
                (split (params :review_time) #";"))))))  )
      ))


;; Local Variables:
;; eval: (rename-buffer "controllers/review.clj")
;; end:
