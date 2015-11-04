(ns cv-generator.markdown-writer
  (:require [stencil.core :as mustache]
            [clojure.java.io :as io]
            [cv-generator.cv-loader :as cv])
  (:use [cv-generator.path]))

(defn export-to-markdown [cv filter filename]
  (with-open [w (io/writer (str path "/target/" filename ".md"))]
    (.write w (mustache/render-file "templates/markdown" (cv/convert-to-data-map cv filter)))))