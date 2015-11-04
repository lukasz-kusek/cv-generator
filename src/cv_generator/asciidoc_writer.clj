(ns cv-generator.asciidoc-writer
  (:require [stencil.core :as mustache]
            [clojure.java.io :as io]
            [cv-generator.cv-loader :as cv])
  (:use [cv-generator.path]))

(defn export-to-asciidoc [cv filter filename]
  (with-open [w (io/writer (str path "/target/" filename ".adoc"))]
    (.write w (mustache/render-file "templates/asciidoc" (cv/convert-to-data-map cv filter))))
  )