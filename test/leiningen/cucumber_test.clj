(ns leiningen.cucumber-test
  (:require [midje.sweet :refer :all]
            [leiningen.cucumber :refer :all]))

(let [project-config {:cucumber-feature-paths ["test/features"]}
      annotated-args {:features ["features" "foo"]
                      :glues ["features/step_definitions" "bar_steps"]
                      :others ["--format" "pretty"
                               "--no-strict"
                               "--dotcucumber" "cucumber.yml"]}]
  (fact
    (config-plugin project-config annotated-args)
    => ["features"
        "foo"
        "--glue"        "features/step_definitions"
        "--glue"        "bar_steps"
        "--format"      "pretty"
        "--no-strict"
        "--dotcucumber" "cucumber.yml"]
    (provided
      (glue-paths annotated-args)    => ["--glue" "features/step_definitions"
                                            "--glue" "bar_steps"]
      (feature-paths annotated-args) => ["features", "foo"])))

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
