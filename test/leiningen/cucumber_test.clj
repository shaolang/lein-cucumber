(ns leiningen.cucumber-test
  (:require [midje.sweet :refer :all]
            [leiningen.cucumber :refer :all]))

(fact
  (group-args->cli-args {:features ["features" "foo"]
                         :glues ["features/step_definitions"]
                         :others ["--format" "pretty"]})
  => ["features" "foo"
      "--glue" "features/step_definitions"
      "--format" "pretty"])

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
  (group-args->cli-args {:features ["foo"], :glues ["bar"], :others nil})
  => ["foo" "--glue" "bar"])
