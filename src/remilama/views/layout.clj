(ns remilama.views.layout
  (:use [hiccup.core :only (html)]
    [hiccup.page :only (html5 include-css include-js)]
    [hiccup.util :only (url)]))

(defn common [title & body]
  (html5
    [:head
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
      [:title title]
      (include-css
	"/stylesheets/bootstrap.min.css"
	"/stylesheets/elusive-webfont.css"
	"/stylesheets/application.css")
      (include-js
	"/javascripts/jquery.js"
	"/javascripts/bootstrap.js")]
    [:body
      [:div {:class "navbar navbar-fixed-top"}
	[:div {:class "navbar-inner"}
	  [:div {:class "container"}
	    [:a {:class "brand" :href (url "/")} "remilama"]]]]
      [:div {:class "container"} body]]))

(defn four-oh-four []
  (common "Page Not Found"
    [:div {:id "four-oh-four"}
    "The page you requested could not be found"]))
