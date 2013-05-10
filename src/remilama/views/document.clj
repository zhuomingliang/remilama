(ns remilama.views.document
  (:use 
    [hiccup.core]
    [hiccup.element]
    [hiccup.form]
    [hiccup.page]
    [hiccup.util :only (url)])
  (:require
    [remilama.views.layout :as layout]
    [ring.middleware.logger :as logger]))

(defn list [documents]
  (layout/common "Remilama documents"
    (link-to {:class "btn btn-primary pull-right"}
      (url "/document/new") "New document")
    (cond
      (empty? documents)
      "No documents."
      :else
      [:table {:class "table"}
	[:tr
	  [:th "Name"]]
	(for [document documents]
	  [:tr
	    [:td
	      (link-to
		(url "/document/show/" (document :id))
		(document :name))]])]
      )))

(defn new-document []
  (layout/common "Remilama documents"
    [:h2 "New document"]
    (form-to {:class "form-horizontal" :enctype "multipart/form-data"}
      [:post (url "/document/create")]
      [:fieldset
	[:div {:class "control-group"}
	  (label {:class "control-label"} "name" "Name")
	  [:div {:class "controls"}
	    (text-field "name")]]
	[:div {:class "control-group"}
	  (label {:class "control-label"} "office-file" "File")
	  [:div {:class "controls"}
	    (file-upload "office-file")]]
	[:div {:class "form-actions"}
	  (submit-button "Upload")]])))

(defn show [doc]
  (layout/common "Remilama documents"
    [:h2 (get doc :name "No title")]
    [:div {:class "pagination"}
      [:ul
	(for [page-no (range 1 (doc :page_count))]
	  [:li
	    [:a {:href "#"} page-no]])]]
    [:div {:class "page-container"}
      [:div { :class "drag-container" }
	[:div {:class "page-content"}]]]
    [:div {:class "modal hide fade dialog-comment"}
      [:div {:class "modal-header"}
	[:button { :type "button" :class "close"
		   :data-dismiss "modal" :aria-hidden "true"} "&times;"]
	[:h3 "Comment"]]
      [:div {:calss "modal-body"}
	(form-to {:class "form-horizontal"} [:post (url "/comments/create")]
	  (text-area {:class "input-xlarge"} "description"))]
      [:div {:class "modal-footer"}
	[:a {:href "#" :class "btn"} "Close"]
	[:a {:href "#" :class "btn btn-primary btn-save"} "Save"]]]
    (include-js
      "/javascripts/Animate.js"
      "/javascripts/Scroller.js"
      "/javascripts/underscore-min.js"
      "/javascripts/backbone-min.js"
      "/javascripts/canvg.js"
      "/javascripts/rgbcolor.js"
      "/javascripts/documents-show.js")))

;; Local Variables:
;; eval: (rename-buffer "views/document.clj")
;; end:
