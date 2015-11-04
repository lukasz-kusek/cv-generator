(ns cv-generator.core
  (:gen-class)
  (:require [cv-generator.cv-loader :as cv])
  (:use [cv-generator.asciidoc-writer]
        [cv-generator.html-writer]
        [cv-generator.markdown-writer]
        [cv-generator.txt-writer]))

(defn generate-cvs [filename]
  (let [cv (cv/load-cv filename)
        filter (cv/load-filter filename)]
    (export-to-asciidoc cv filter filename)
    (export-to-html cv filter filename)
    (export-to-markdown cv filter filename)
    (export-to-txt cv filter filename))
  )

(generate-cvs "KusekLukaszCV")
