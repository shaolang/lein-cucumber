(defproject lein-cucumber "2.0.0-SNAPSHOT"
  :description "Run cucumber-jvm specifications with leiningen"
  :profiles {:cucumber {:dependencies [[commons-io "2.0"]]
                        :plugins [[lein-cucumber "2.0.0-SNAPSHOT"]]}
             :dev {:dependencies [[info.cukes/cucumber-clojure "1.1.7-SNAPSHOT"]
                                  [midje "1.6.2"]]
                   :plugins [[lein-midje "3.1.3"]]}
             :1.1.6 {:dependencies [[info.cukes/cucumber-clojure "1.1.6"]]}
             :1.1.4 {:dependencies [[info.cukes/cucumber-clojure "1.1.4"]]}
             :1.1.3 {:dependencies [[info.cukes/cucumber-clojure "1.1.3"]]}
             :1.1.2 {:dependencies [[info.cukes/cucumber-clojure "1.1.2"]]}
             :1.1.1 {:dependencies [[info.cukes/cucumber-clojure "1.1.1"]]}}
  :eval-in-leiningen true
  :license {:name "Unlicense"
            :url "http://unlicense.org/"
            :distribution :repo})
