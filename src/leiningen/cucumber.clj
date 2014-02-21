(ns leiningen.cucumber
  (:require [clojure.java.io :refer [file]]
            [leiningen.core.eval :refer [eval-in-project]])
  (:import [cucumber.api.cli Main]))

(defn cucumber
  [project & args]
  (eval-in-project
    (update-in project [:source-paths] conj (file "features" "step_definitions"))
    `(Main/main (into-array String
                            ["features"
                             "--glue" "features/step_definitions"]))))
