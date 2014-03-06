(ns leiningen.cucumber-test
  (:require [midje.sweet :refer :all]
            [leiningen.cucumber :refer :all]))

#_(let [project-config {:cucumber-feature-paths ["test/features"]}
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

(let [args-map {:features ["features" "foo"]
                :glues ["features/step_definitions"]
                :others ["--format" "pretty"]}]
  (fact
    (group-args->cli-args args-map)
    => ["features" "foo"
        "--glue" "features/step_definitions"
        "--format" "pretty"]))

(fact "include project configs for missing features"
      (include-project-config-for-missing-features {} {:features nil})
      => {:features nil}

      (include-project-config-for-missing-features
        {:cucumber-feature-paths ["foo"]} {:features nil})
      => {:features ["foo"]}

      (include-project-config-for-missing-features
        {:cucumber-feature-paths ["foo"]} {:features ["bar"]})
      => {:features ["bar"]})

(fact "include plugin defaults for missing configs"
      (include-plugin-defaults-for-missing-configs {:features nil, :glues nil})
      => (contains {:features ["features"], :glues ["features/step_definitions"]})

      (include-plugin-defaults-for-missing-configs
        {:features ["foo"], :glues nil})
      => (contains {:features ["foo"], :glues ["foo/step_definitions"]})

      (include-plugin-defaults-for-missing-configs
        {:features nil, :glues ["bar"]})
      => (contains {:features ["features"], :glues ["bar"]})

      (include-plugin-defaults-for-missing-configs
        {:features ["foo"], :glues ["bar"]})
      => (contains {:features ["foo"], :glues ["bar"]}))

(facts
  (group-args [])
  => {:features nil, :glues nil, :others nil}

  (group-args ["--GLUE" "FOO"])
  => {:features nil, :glues ["FOO"], :others nil}

  (group-args ["--dotcucumber" "cucumber.yml"])
  => {:features nil, :glues nil, :others ["--dotcucumber" "cucumber.yml"]}

  (group-args ["features"])
  => {:features ["features"], :glues nil, :others nil}

  (group-args ["--no-strict"])
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
