(ns cv-generator.html-writer
  (:require [stencil.core :as mustache]
            [clojure.java.io :as io]
            [cv-generator.cv-loader :as cv]
            [clj-time.format :as time-format]
            [clj-time.core :as time]
            [clojure.string :as string])
  (:use [cv-generator.path])
  (:import (org.joda.time DateTime)
           (org.joda.time.format DateTimeFormat)))

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

(defn add-percentage [skills max-rank]
  (map #(merge % {:percentage (* 100 (/ (:points %) max-rank))}) skills))

(defn add-percentage-to-skills-in-category [category max-rank]
  {:name   (:name category)
   :skills (add-percentage (:skills category) max-rank)})

(defn max-rank-in-category [category]
  (apply max (map #(:points %) (:skills category))))

(defn max-rank-in-categories [categories]
  (apply max (map #(max-rank-in-category %) categories)))

(defn add-percentage-to-skills-in-categories [categories]
  (let [max-rank (max-rank-in-categories categories)]
    (println max-rank)
    (map #(add-percentage-to-skills-in-category % max-rank) categories)))

(defn add-achievements-empty-list-marker [experience]
  (merge experience {:achievements? (not (empty? (:achievements experience)))})
  )

(defn convert-to-data-map [cv filter filename]
  {:cv-name         filename
   :title           {:html        (str (:first-name cv) " " (:sur-name cv) " - " (:title (:summary cv)))
                     :first-part  (string/join " " (butlast (string/split (:title (:summary cv)) #" ")))
                     :second-part (last (string/split (:title (:summary cv)) #" "))}
   :first-name      (:first-name cv)
   :sur-name        (:sur-name cv)
   :contact         (:contact cv)
   :years           {:first-start-date (first-start-date cv)
                     :first-year       (time/year (time-format/parse (first-start-date cv)))
                     :last-year        (time/year (time-format/parse (last-start-date cv)))
                     :years            (take-nth 2 (start-dates-and-years (start-dates-without-first-and-last cv)))}
   :summary         (:summary cv)
   :availability    (:availability cv)
   :skills          (:skills cv)
   :categories      (add-percentage-to-skills-in-categories (cv/skills-grouped-by-categories cv filter))
   :experience      (map add-achievements-empty-list-marker (:experience cv))
   :recommendations (:recommendations cv)
   :learning        (:learning cv)
   :last-updated    (. (DateTimeFormat/shortDate) print (new DateTime))})

(defn export-to-html [cv filter filename]
  (with-open [w (io/writer (str path "/target/" filename ".html"))]
    (.write w (mustache/render-file "templates/html" (convert-to-data-map cv filter filename)))))