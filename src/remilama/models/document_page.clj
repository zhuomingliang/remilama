(ns remilama.models.document-pages
  (:use [korma.core]))

(defentity document_pages)

(defn find-by-page-no [documents-id page-no]
  (first (select document_pages
    (where {:documents_id documents-id :page_no: page-no}))))
