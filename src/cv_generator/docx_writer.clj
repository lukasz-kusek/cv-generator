(ns cv-generator.docx-writer
  (:require [stencil.core :as mustache]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [cv-generator.cv-loader :as cv])
  (:use [cv-generator.path])
  (:import (java.util.zip ZipEntry ZipOutputStream)
           (java.io File)))

(defn zip-directory [directory filename]
  (with-open [zip (ZipOutputStream. (io/output-stream filename))]
    (doseq [f (file-seq (io/file directory)) :when (.isFile f)]
      (let [name (.toString (.relativize (.toPath (File. directory)) (.toPath f)))]
        (println name)
        (.putNextEntry zip (ZipEntry. name))
        (io/copy f zip)
        (.closeEntry zip)))))

(defn export-to-docx [cv filter filename]
  (let [target-directory (str path "/target/" filename "/docx")
        source-directory (str path "/resources/templates/docx")
        document (str target-directory "/word/document.xml")]
    (fs/delete-dir target-directory)
    (fs/copy-dir source-directory target-directory)
    (with-open [w (io/writer document)]
      (.write w (mustache/render-file "templates/docx" (cv/convert-to-data-map cv filter))))
    (zip-directory target-directory (str path "/target/" filename ".docx"))
    )
  )