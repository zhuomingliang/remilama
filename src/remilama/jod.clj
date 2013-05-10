(ns remilama.jod
  "Convert office documents to PDF."
  (:import (org.artofsolving.jodconverter.office
	      DefaultOfficeManagerConfiguration)
	   (org.artofsolving.jodconverter
	      OfficeDocumentConverter)))

(defn- global-office-manager []
  (let [instance (atom nil)
	 make-instance
	 (fn [_]
	   (let [office-manager (.buildOfficeManager
				 (DefaultOfficeManagerConfiguration.))]
	     (.start office-manager)
	     office-manager))]
    (fn [] (or @instance (swap! instance make-instance)))))
	 

(def office-manager (global-office-manager))

(defn converter [office-manager]
  (OfficeDocumentConverter. office-manager))

(defn convert [office-file pdf-file]
  (let [converter (converter (office-manager))]
    (.convert converter office-file pdf-file)))
