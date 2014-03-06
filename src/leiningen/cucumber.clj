(ns leiningen.cucumber
  (:require [clojure.java.io :refer [file]]
            [clojure.string :refer [lower-case]]
            [leiningen.core.eval :refer [eval-in-project]])
  (:import [cucumber.api.cli Main]))

(defn lower-case-option [arg]
  (if (.startsWith arg "-")
    (lower-case arg)
    arg))

(defn categorize-option [arg]
  (case arg
    ("-g" "--glue") :glues
    ("--dotcucumber" "-f" "--format" "-n" "--name"   "--snippets" "-t" "--tag") :opt-with-arg
    (if (.startsWith arg "-")
      :opt
      nil)))

(defn arg-type [cat prev-cat]
  (cond
    (= prev-cat :opt-with-arg)                    :opt-with-arg
    (= prev-cat :glues)                           :glues
    (and (nil? cat)
         (or (nil? prev-cat) (= prev-cat :opt)))  :features
    :else                                         cat))

(defn annotate-opt-args [arg-cats]
  (->> arg-cats
    (cons nil)
    (map arg-type arg-cats)))

(defn recategorize-non-glues-and-features-as-others [{:keys [category] :as arg-map}]
  (assoc arg-map :category (if (some #{category} [:glues :features])
                             category
                             :others)))

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

(defn feature-paths [{:keys [features]}]
  (if (seq features)
    features
    ["features"]))

(defn glue-paths [{:keys [features glues] :as args}]
  (interleave (repeat "--glue")
              (cond
                (seq glues)    glues
                (seq features) (map #(str % "/step_definitions") features)
                :else          (map #(str % "/step_definitions")
                                    (feature-paths args)))))

(defn add-project-features-if-not-given [project {:keys [features] :as args}]
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

(defn config-plugin [project args]
  (->> args
    (add-project-features-if-not-given project)
    ((juxt feature-paths glue-paths :others))
    concat
    flatten
    ((partial remove nil?))))

(defn group-args->cli-args [group-args]
  (->> group-args
    ((juxt :features
           #(interleave (repeat "--glue") (:glues %))
           :others))
    concat
    flatten))

(defn cucumber
  [project & args]
  (let [arg-groups (group-args args)
        cuke-args (vec (config-plugin project arg-groups))
        glues (->> cuke-args
                (drop-while #(not (.startsWith % "-")))
                (partition 2)
                (take-while #(= (first %) "--glue"))
                (map fnext))]
    (eval-in-project
      (update-in project [:source-paths] concat glues)
      `(Main/main (into-array String ~cuke-args)))))
