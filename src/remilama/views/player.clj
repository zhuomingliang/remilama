(ns remilama.views.player
  (:use 
    [noir.validation]
    [taoensso.tower :only (with-locale with-scope t style)]
    [hiccup.core]
    [hiccup.element]
    [hiccup.form]
    [hiccup.page]
    [hiccup.util :only (url)])
  (:require
    [remilama.views.layout :as layout]))

(defn list [players]
  (layout/common "Remilama Players"
    (link-to {:class "btn btn-primary pull-right"}
      (url "/player/new") "New player")
    (cond
      (empty? players) ""
      :else
      [:table {:class "table"}
	[:tr
	  [:th "Name"]
	  [:th "Email Address"]]
	(for [player players]
	  [:tr
	    [:th (player :name)]
	    [:th (player :email)]])])))

(defn show [player]
  ())

(defn- player-form [player]
  [:fieldset
    [:div {:class (if (errors? :name) "control-group error" "control-group")}
      [:label {:class "control-label"} (t :labels/name)]
      [:div {:class "controls"}
        (text-field "name" (player :name))
        (on-error :name (fn [[err]] [:span {:class "help-inline"} err]))]]
    [:div {:class (if (errors? :email) "control-group error" "control-group")}
      [:label {:class "control-label"} (t :labels/email)]
      [:div {:class "controls"}
        (text-field "email" (player :email))
        (on-error :email (fn [[err]] [:span {:class "help-inline"} err]))]]])

(defn new-player [player]
  (layout/common "New player"
    (form-to {:class "form-horizontal"}
      [:post (url "/player/create")]
      (player-form player)
      [:div {:class "form-actions"}
	(submit-button {:class "btn btn-primary"}
	  "Register")])))

(defn login []
  (layout/common "Login"
    (form-to [:post "/login"]
      [:div
        (label "username" "user name:")
        (text-field "username" "kawasima")]
      [:div
        (label "password" "password:")
        (password-field "password" "password")]
      [:div {:class "form-actions"}
        (submit-button (t :buttons/login))])))

(defn dashboard []
  (layout/common "Dashboard"
    [:ul
      [:li
        (link-to "/document/" (t :labels/documents))]
      [:li
        (link-to "/review/" (t :labels/reviews))]]))

;; Local Variables:
;; eval: (rename-buffer "views/player.clj")
;; end:
