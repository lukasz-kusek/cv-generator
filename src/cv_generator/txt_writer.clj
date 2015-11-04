(ns cv-generator.txt-writer
  (:require [stencil.core :as mustache]
            [clojure.java.io :as io]
            [cv-generator.cv-loader :as cv])
  (:use [cv-generator.path]))

(defn export-to-txt [cv filter filename]
  (with-open [w (io/writer (str path "/target/" filename ".txt"))]
    (.write w (mustache/render-file "templates/txt" (cv/convert-to-data-map cv filter)))))