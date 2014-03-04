(defproject lein-cucumber "2.0.0-SNAPSHOT"
  :description "Run cucumber-jvm specifications with leiningen"
  :dependencies [[info.cukes/cucumber-clojure "1.1.6-SNAPSHOT"]
                 [leiningen-core "2.3.4"]]
  :profiles {:cucumber {:dependencies [[commons-io "2.0"]]
                        :plugins [[lein-cucumber "2.0.0-SNAPSHOT"]]}
             :dev {:dependencies [[midje "1.6.2"]]
                   :plugins [[lein-midje "3.1.3"]]}}
  :eval-in :leiningen
  :license {:name "Unlicense"
            :url "http://unlicense.org/"
            :distribution :repo})
