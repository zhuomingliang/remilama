(ns remilama.controllers.document
  (:use
    [compojure.core :only (defroutes GET POST)]
    [clojure.java.shell :only (sh)])
  (:require
    [clojure.string :as str]
    [ring.util.response :as response]
    [ring.middleware.multipart-params :as mp]
    [cemerick.friend :as friend]
    [clojure.java.io :as io]
    [me.raynes.fs :as fs]
    [remilama.views.document :as view]
    [remilama.models.document :as Document]
    [remilama.jod :as jod]))

(defn index []
  (let [documents (Document/find-all)]
    (view/list documents)))

(defn new-document []
  (view/new-document))

(defn create [file]
  "create new document."
  (let
    [ office-file (io/file "work/office" (file :filename))
      pdf-file (io/file "work/pdf"
		 (str (fs/base-name
			(file :filename)
			(fs/extension (file :filename))) ".pdf"))]
    (io/copy (file :tempfile) office-file)
    (jod/convert office-file pdf-file)
    (let [res (Document/insert
	       { :name (file :filename) })
	   id (res :GENERATED_KEY)
	   svg-dir (io/file "work/svg" (str id))]
      (if (not (fs/exists? svg-dir)) (fs/mkdir svg-dir))
      (sh "pdf2svg"
	(.getAbsolutePath pdf-file)
	(str (.getAbsolutePath svg-dir) "/%03d.svg") "all")
      (Document/update {:id id, ::page_count (count (.listFiles svg-dir))}))
    (response/redirect "/document/")))

(defn show [id]
  (let [ doc (Document/find id)
	 svg-dir (io/file "work/svg" id)]    
    (view/show doc)))

(defn page [documents-id page-no]
  (let [ svg-dir (io/file "work/svg" documents-id)
	 page-file (io/file svg-dir (format "%03d.svg" page-no))]
    (->
      (response/response (new java.io.FileInputStream page-file))
      (response/content-type "image/svg+xml")
      (response/header "Content-Length" (fs/size page-file)))))

(defroutes routes
  (GET "/" [] (index))
  (GET "/show/:id" [id] (show id))
  (GET ["/show/:id/:page-no.svg", :page-no "[0-9]+"]
    [id page-no] (page id (Integer/parseInt page-no)))
  (GET "/new" [] (new-document))
  (POST "/create" {params :params} (create (get params :office-file))))

;; Local Variables:
;; eval: (rename-buffer "controllers/document.clj")
;; end:
