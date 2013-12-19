(ns leiningen.cucumber.util
  (:require [clojure.java.io :refer [file]])
  (:import [cucumber.runtime.formatter FormatterFactory]
           [cucumber.runtime.io MultiLoader ResourceLoaderClassFinder]
           [cucumber.runtime.model CucumberFeature]
           [cucumber.runtime Env CucumberException RuntimeOptions]
           [java.util Arrays Properties]))

(defn new-runtime-options [args]
  (RuntimeOptions. (Arrays/asList(into-array String args))))

(defn- create-runtime-options [feature-paths glue-paths target-path args]
  (let [runtime-options (new-runtime-options args)
        formatter-factory (FormatterFactory.)]
    (when (.. runtime-options getFeaturePaths (isEmpty))
      (.. runtime-options getFeaturePaths (addAll feature-paths)))
    (when (.. runtime-options getGlue (isEmpty))
      (.. runtime-options getGlue (addAll glue-paths)))
    (doto (.getFormatters runtime-options)
      (.add (.create formatter-factory (str "pretty:"
                                            (.getAbsolutePath (file target-path
                                                                    "test-reports"
                                                                    "cucumber.out"))))))
    runtime-options))

(defn- create-runtime [runtime-options]
  (let [classloader (.getContextClassLoader (Thread/currentThread))
        resource-loader (MultiLoader. classloader)]
    (cucumber.runtime.Runtime. resource-loader
                               (ResourceLoaderClassFinder. resource-loader
                                                           classloader)
                               classloader runtime-options)))

(defn run-cucumber! [feature-paths glue-paths target-path args]
  (let [runtime-options (create-runtime-options feature-paths glue-paths
                                                target-path args)
        runtime (create-runtime runtime-options)]
    (println "Running cucumber...")
    (println "Looking for features in: " (vec (.getFeaturePaths runtime-options)))
    (println "Looking for glue in: " (vec (.getGlue runtime-options)))
    (try
      (.run runtime)
      (catch CucumberException e
        (println (.getMessage e))))
    runtime))
