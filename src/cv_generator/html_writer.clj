(ns cv-generator.html-writer
  (:require [stencil.core :as mustache]
            [clojure.java.io :as io]
            [cv-generator.cv-loader :as cv]
            [clj-time.format :as time-format]
            [clj-time.core :as time]
            [clojure.string :as string])
  (:use [cv-generator.path]))

(defn sorted-start-dates [cv]
  (sort (map #(:start-date %) (:experience cv))))

(defn first-start-date [cv]
  (first (sorted-start-dates cv)))

(defn last-start-date [cv]
  (last (sorted-start-dates cv)))

(defn start-dates-without-first-and-last [cv]
  (butlast (rest (sorted-start-dates cv))))

(defn start-date-and-year [start-date]
  {:start-date start-date
   :year       (time/year (time-format/parse start-date))
   })

(defn start-dates-and-years [start-dates]
  (map start-date-and-year start-dates))

(defn add-percentage [skills]
  (let [max-rank (apply max (map #(:points %) skills))]
    (map #(merge % {:percentage (* 100 (/ (:points %) max-rank))}) skills))
  )

(defn add-percentage-to-skills-in-category [category]
  {:name   (:name category)
   :skills (add-percentage (:skills category))})

(defn add-percentage-to-skills-in-categories [categories]
  (map add-percentage-to-skills-in-category categories))

(defn convert-to-data-map [cv filter filename]
  {:cv-name    filename
   :title      {:html        (str (:first-name cv) " " (:sur-name cv) " - " (:title (:summary cv)))
                :first-part  (string/join " " (butlast (string/split (:title (:summary cv)) #" ")))
                :second-part (last (string/split (:title (:summary cv)) #" "))}
   :first-name (:first-name cv)
   :sur-name   (:sur-name cv)
   :contact    (:contact cv)
   :years      {:first-start-date (first-start-date cv)
                :first-year       (time/year (time-format/parse (first-start-date cv)))
                :last-year        (time/year (time-format/parse (last-start-date cv)))
                :years            (start-dates-and-years (start-dates-without-first-and-last cv))}
   :summary    (:summary cv)
   :categories (add-percentage-to-skills-in-categories (cv/skills-grouped-by-categories cv filter))
   :experience (:experience cv)
   :learning   (:learning cv)})

(defn export-to-html [cv filter filename]
  (with-open [w (io/writer (str path "/target/" filename ".html"))]
    (.write w (mustache/render-file "templates/html" (convert-to-data-map cv filter filename)))))