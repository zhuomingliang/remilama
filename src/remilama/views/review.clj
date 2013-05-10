(ns remilama.views.review
  (:use 
    [noir.validation]
    [taoensso.tower :as tower :only (with-locale with-scope t style)]
    [hiccup.core]
    [hiccup.element]
    [hiccup.form]
    [hiccup.page]
    [hiccup.util :only (url)])
  (:require
    [remilama.views.layout :as layout]))


(defn list [reviews]
  (layout/common "Remilama Reviews"
    (link-to {:class "btn btn-primary pull-right"}
      (url "/review/new") "New review")
    (cond
      (empty? reviews) ""
      :else
      [:table {:class "table"}
	[:tr
	  [:th "Name"]
	  [:th (t :labels/review-date)]]
	(for [review reviews]
	  [:tr
	    [:th (link-to (url "/review/show/" (review :id)) (review :name))]
	    [:th (tower/format-date (review :review_date))]])])))

(defn show [review]
  (layout/common (review :name)
    (form-to {:class "form-horizontal"} [:post ""]
      [:fieldset
        [:div
          [:label {:class "control-label"} (t :labels/name)]
          [:div {:class "controls"} (review :name)]]]
      [:div {:class "form-actions"}
        (link-to {:class "btn btn-primary"}
          (url "/review/edit/" (review :id))
          (t :buttons/edit))])))

(defn- review-form [review players documents]
  [:fieldset
    [:div {:class (if (errors? :name) "control-group error" "control-group")}
      [:label {:class "control-label"} (t :labels/name)]
      [:div {:class "controls"}
        (text-field "name" (review :name))
        (on-error :name (fn [[err]] [:span {:class "help-inline"} err]))]]

    [:div {:class (if (errors? :review_date)
                    "control-group error" "control-group date")}
      [:label {:class "control-label"} (t :labels/review-date)]
      [:div {:id "review-date-picker" :class "controls input-append date"}
        (text-field {:data-format "yyyy-MM-dd"} "review_date" (review :review_date))
        [:span {:class "add-on"}
          [:i { :data-time-icon "icon-time"
                :data-date-icon "icon-calendar"}]]
        (on-error :review_date (fn [[err]] [:span {:class "help-inline"} err]))]]

    [:div {:class (if (errors? :review_time) "control-group error" "control-group")}
      [:label {:class "control-label"} (t :labels/review-time)]
      [:div {:class "controls"}
        (text-field "review_time"
          (str 
            (review :begin_at)
            ";"
            (review :end_at)))
        (on-error :review_time (fn [[err]] [:span {:class "help-inline"} err]))]]
    
    [:div {:class (if (errors? :participants) "control-group error" "control-group")}
      [:label {:class "control-label"} (t :labels/participants)]
      [:div {:class "controls"}
        (drop-down {:id "participants" :multiple "multiple" :class "input-xlarge"} "participants[]"
          (map #(vector (% :name) (% :id)) players))
        (on-error :participants (fn [[err]] [:span {:class "help-inline"} err]))]]

    [:div {:class (if (errors? :review_documents) "control-group error" "control-group")}
      [:label {:class "control-label"} (t :labels/review-documents)]
      [:div {:class "controls"}
        (drop-down {:multiple "multiple" :class "input-xlarge"} "review_documents"
          (map #(vector (% :name) (% :id)) documents))
        (on-error :review_documents (fn [[err]] [:span {:class "help-inline"} err]))]]

    (include-js
      "/javascripts/select2.min.js"
      "/javascripts/jquery.slider.all.js"
      "/javascripts/bootstrap-datetimepicker.min.js")
    (include-css
      "/stylesheets/select2.css"
      "/stylesheets/jquery.slider.min.css"
      "/stylesheets/bootstrap-datetimepicker.min.css")
    (javascript-tag "$(function() {$(':input[name=review_time]').slider({
    from: 480,
    to: 1080,
    step: 15,
    dimension: '',
    scale: ['8:00','9:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00'],
    limits: false,
    calculate: function(value) {
      var hours = Math.floor( value / 60 );
      var mins = ( value - hours*60 );
      return (hours < 10 ? '0'+hours : hours) + ':' + ( mins == 0 ? '00' : mins );
    }
  });
  $('#review-date-picker').datetimepicker({pickTime:false});
  $('select#participants').select2();
  $('select[name=review_documents]').select2();
  });")])


(defn new-review [review players documents]
  (layout/common "New review"
    (form-to {:class "form-horizontal"}
      [:post (url "/review/create")]
      (review-form review players documents)
      [:div {:class "form-actions"}
	(submit-button {:class "btn btn-primary"}
	  (t :buttons/save))])))

(defn edit [review players documents]
  (layout/common "Edit review"
    (form-to {:class "form-horizontal"}
      [:post (url "/review/update/" (review :id))]
      (review-form review players documents)
      [:div {:class "form-actions"}
	(submit-button {:class "btn btn-primary"}
	  (t :buttons/save))])))

;; Local Variables:
;; eval: (rename-buffer "views/review.clj")
;; end:
