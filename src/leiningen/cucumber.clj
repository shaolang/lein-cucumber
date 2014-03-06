(ns leiningen.cucumber
  (:require [clojure.java.io :refer [file]]
            [clojure.string :refer [lower-case]]
            [leiningen.core.eval :refer [eval-in-project]])
  (:import [cucumber.api.cli Main]))

(defn lower-case-option [arg]
  (if (.startsWith arg "-") (lower-case arg) arg))

(defn categorize-option [arg]
  (case arg
    ("-g" "--glue") :glues
    ("--dotcucumber" "-f" "--format" "-n" "--name"   "--snippets" "-t" "--tag") :opt-with-arg
    (if (.startsWith arg "-") :opt nil)))

(defn arg-type [cat prev-cat]
  (cond
    (= prev-cat :opt-with-arg)                    :opt-with-arg
    (= prev-cat :glues)                           :glues
    (and (nil? cat)
         (or (nil? prev-cat) (= prev-cat :opt)))  :features
    :else                                         cat))

(defn annotate-opt-args [arg-cats]
  (->> arg-cats (cons nil) (map arg-type arg-cats)))

(defn recategorize-non-glues-and-features-as-others [{:keys [category] :as arg-map}]
  (assoc arg-map :category
         (if (some #{category} [:glues :features]) category :others)))

(defn group-args [args]
  (->> args
    (map lower-case-option)
    (map categorize-option)
    annotate-opt-args
    (map #(hash-map :arg % :category %2) args)
    (remove #(and (= (:category %) :glues) (.startsWith (:arg %) "-")))
    (map recategorize-non-glues-and-features-as-others)
    (group-by :category)
    (reduce #(assoc % (key %2) (map :arg (val %2))) {})
    (merge {:features nil, :glues nil, :others nil})))

(defn include-project-config-for-missing-configs
  [project {:keys [features] :as args}]
  (if features
    args
    (assoc args :features (:cucumber-feature-paths project))))

(defn include-project-config-for-missing-features
  [{:keys [cucumber-feature-paths]} {:keys [features] :as args-map}]
  (if (seq features)
    args-map
    (assoc args-map :features cucumber-feature-paths)))

(defn include-plugin-defaults-for-missing-configs
  [{:keys [features glues others]}]
  (let [final-features (if (seq features) features ["features"])
        final-glues (if (seq glues)
                      glues
                      (map #(str % "/step_definitions") final-features))]
    {:features final-features
     :glues final-glues
     :others others}))

(defn group-args->cli-args [group-args]
  (->> group-args
    ((juxt :features
           #(interleave (repeat "--glue") (:glues %))
           :others))
    concat
    flatten
    (remove nil?)
    vec))

(defn cucumber
  [project & args]
  (let [arg-maps (->> args
                   group-args
                   (include-project-config-for-missing-configs project)
                   include-plugin-defaults-for-missing-configs)
        cli-args (group-args->cli-args arg-maps)]
    (eval-in-project
      (update-in project [:source-paths] concat (:glues arg-maps))
      `(Main/main (into-array String ~cli-args)))))
