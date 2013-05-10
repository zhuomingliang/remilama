(defproject remilama "0.1.0-SNAPSHOT"
  :description "A review tool"
  :url "https://github.com/kawasima/remilama.git"
  :dependencies [ [org.clojure/clojure "1.4.0"]
                  [org.clojure/tools.nrepl "0.2.2"]
                  [com.cemerick/friend "0.1.5"] ;; authentication (must be before compojure)
		  [compojure "1.1.5"]           ;; routing
                  [lib-noir "0.5.0"]            ;; validation, session
                  [com.taoensso/tower "1.5.1"]  ;; i18n
		  [hiccup "1.0.3"]              ;; template
		  [korma "0.3.0-RC5"]           ;; database access
                  [ritz/ritz-nrepl-middleware "0.7.0"] ;; debugging
		  [me.raynes/fs "1.4.0"]
                  [http-kit "2.1.1"]
		  [mysql/mysql-connector-java "5.1.24"]
		  [ring.middleware.logger "0.4.0"]]
  :main remilama.core
  :plugins [ [lein-ring "0.8.2"]
             [lein-ritz "0.7.0"]]
  :repl-options {:nrepl-middleware
                  [ ritz.nrepl.middleware.javadoc/wrap-javadoc
                    ritz.nrepl.middleware.simple-complete/wrap-simple-complete]}
  :resource-paths ["lib/*", "resources"]
  :ring {:handler remilama.core/application}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})

