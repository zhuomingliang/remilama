(ns remilama.controllers.player
  (:use
    [compojure.core :only (defroutes GET POST)]
    [hiccup.util :only (url)]
    [noir.validation])
  (:require
    [ring.util.response :as response]
    [remilama.views.player :as view]
    [cemerick.friend :as friend]
    [remilama.models.player :as Player]))  

(defn index []
  (let [players (Player/find-all)]
    (view/list players)))

(defn show [id]
  (let [player (Player/find id)]
    (view/show player)))

(defn new-player []
  (view/new-player {}))

(defn create [params]
  (rule (has-value? (params :name))
          [:name "name must have a value"])
  (rule (has-value? (params :email))
          [:email "email must have a value"])
  (rule (is-email? (params :email))
          [:email "email is wrong format"])
  
  (if (errors?)
    (view/new-player (merge params {:errors @*errors*}))
    (do
      (Player/insert params)
      (response/redirect (url "/player/")))))

(defroutes routes
  (GET "/player/" [] (index))
  (GET "/player/show/:id" [id] (show id))
  (GET "/player/new" [] (new-player))
  (POST "/player/create" {params :params} (create params))
  (GET "/login" [] (view/login))
  (GET "/" request (friend/authorize #{::remilama.core/user} (view/dashboard) )))

;; Local Variables:
;; eval: (rename-buffer "controllers/player.clj")
;; end:
