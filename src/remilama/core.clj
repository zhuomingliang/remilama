(ns remilama.core
  (:use
    [compojure.core :as compojure :only (GET POST ANY defroutes)]
    [korma.db :only (defdb mysql)]
    [clojure.tools.nrepl.server :only (start-server stop-server)]
    [org.httpkit.server :only (run-server)])
  (:require [compojure.handler :as handler]
    [compojure.route :as route]
    [taoensso.tower :as tower]
    [taoensso.tower.ring :as tower-ring]
    [cemerick.friend :as friend]
    (cemerick.friend
      [workflows :as workflows]
      [credentials :as creds])
    [noir.validation :as validation]
    [ring.middleware.reload :as reload]
    [remilama.controllers
      [document :as document]
      [review :as review]
      [review-comment :as review-comment]
      [player :as player]]
    [remilama.views.layout :as layout]))

(defdb prod (mysql { :db "remilama"
		     :user "remilama"
		     :password "remilama"}))

(tower/load-dictionary-from-map-resource!)


(defroutes routes
  (compojure/context "/document" []
    (friend/wrap-authorize document/routes #{::user})) 
  review/routes
  player/routes
  (route/resources "/")
  (route/not-found (layout/four-oh-four)))  

(def application
  (-> routes
    (friend/authenticate { :credential-fn (fn [map]
                                            (-> map
                                              (assoc :roles #{::user})
                                              (dissoc map :password)))
                           :workflows [(workflows/interactive-form)]})
    tower-ring/wrap-i18n-middleware
    validation/wrap-noir-validation))

(defn in-dev? [args] true)
(defn -main [& args]
  (let [h (if (in-dev? args)
                  (reload/wrap-reload (handler/site #'application))
                  (handler/site application))]
    (run-server h {:port 8080})))

