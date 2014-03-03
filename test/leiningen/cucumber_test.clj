(ns leiningen.cucumber-test
  (:require [midje.sweet :refer :all]
            [leiningen.cucumber :refer :all]))

(let [project-config {:cucumber-feature-paths ["test/features"]}
      args-given     ["--glue"       "features/step_definitions"
                      "features"
                      "--format"     "pretty"
                      "foo"
                      "--no-strict"
                      "--g"          "bar_steps"
                      "--dotcucmber" "cucumber.yml"]
      annotated-args {:features ["features" "foo"]
                      :glues ["features/step_definitions" "bar_steps"]
                      :others ["--format" "pretty"
                               "--no-strict"
                               "--dotcucumber" "cucumber.yml"]}
      include-project-config (update-in annotated-args
                                        [:features]
                                        conj
                                        "test/features")]

  (fact
    (config-plugin project-config args-given)
    => ["features"
        "foo"
        "test/features"
        "--glue"        "features/step_definitions"
        "--glue"        "bar_steps"
        "--format"      "pretty"
        "--no-strict"
        "--dotcucumber" "cucumber.yml"]
    (provided
      (annotate-args args-given)        => annotated-args
      (glue-paths include-project-config)    => ["--glue" "features/step_definitions"
                                            "--glue" "bar_steps"]
      (feature-paths include-project-config) => ["features", "foo", "test/features"]
      (others include-project-config)   => ["--format" "pretty"
                                            "--no-strict"
                                            "--dotcucumber" "cucumber.yml"])))

(facts
  (annotate-args [])
  => {:features nil, :glues nil, :others nil}

  (annotate-args ["--GLUE" "FOO"])
  => {:features nil, :glues ["FOO"], :others nil}

  (annotate-args ["--dotcucumber" "cucumber.yml"])
  => {:features nil, :glues nil, :others ["--dotcucumber" "cucumber.yml"]}

  (annotate-args ["features"])
  => {:features ["features"], :glues nil, :others nil}

  (annotate-args ["--no-strict"])
  => {:features nil, :glues nil, :others ["--no-strict"]})

(facts
  (glue-paths {:glues ["foo"]})
  => ["--glue" "foo"]

  (glue-paths {})
  => ["--glue" "features/step_definitions"]

  (glue-paths {:features nil})
  => ["--glue" "features/step_definitions"])

(facts
  (feature-paths {:features ["features"]})
  => ["features"]

  (feature-paths {:features ["test/features" "features"]})
  => ["test/features" "features"]

  (feature-paths nil)
  => ["features"])


(facts "handling of other arguments given"
       (others {:others nil})
       => nil

       (others {:others ["--no-strict" "--format" "pretty"]})
       => ["--no-strict" "--format" "pretty"])
