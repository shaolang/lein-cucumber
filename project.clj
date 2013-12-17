(defproject lein-cucumber "1.0.3-SNAPSHOT"
  :description "Run cucumber-jvm specifications with leiningen"
  :dependencies [[info.cukes/cucumber-clojure "1.1.1"]
                 [leiningen-core "2.0.0"]]
  :profiles {:cucumber {:dependencies [[commons-io "2.0"]]
                        :plugins [[lein-cucumber "1.0.3-SNAPSHOT"]]}}
  :eval-in :leiningen
  :license {:name "Unlicense"
            :url "http://unlicense.org/"
            :distribution :repo})
