(ns cv-generator.cv-loader
  (:require [cheshire.core :as json]
            [clj-time.format :as time-format]
            [clj-time.core :as time]
            [clojure.math.numeric-tower :as math]
            [clojure.string :as string])
  (:use [cv-generator.path])
  (:import (org.joda.time DateTime Months)
           (org.joda.time.format DateTimeFormat))
  )

(def frequencies
  {"day-to-day"   1
   "often"        0.75
   "occasionally" 0.5})

(defn factor [frequency]
  (frequencies frequency))

(defn load-cv [filename]
  (json/parse-stream (clojure.java.io/reader (str path "/resources/" filename ".json")) true))

(defn load-filter [filename]
  (set (json/parse-stream (clojure.java.io/reader (str path "/resources/" filename "-filter.json")))))

(defn now []
  (new DateTime))

(defn to-date-time [string]
  (time-format/parse string))

(defn get-or-default [map key default]
  (let [value (key map)]
    (if (nil? value) default (to-date-time value))))

(defn points-for-month [number-of-months]
  (math/expt 2 (- (/ number-of-months 12))))

(defn calculate-points [factor start-inclusive end-exclusive]
  (* factor (reduce + (map points-for-month (range start-inclusive end-exclusive)))))

(defn months-since [^DateTime date]
  (let [now (new DateTime)]
    (.getMonths (Months/monthsBetween date now))))

(defn update-skill [skill default-start-date default-end-date]
  (let [start-date (get-or-default skill :start-date default-start-date)
        end-date (get-or-default skill :end-date default-end-date)
        factor (factor (:usage-frequency skill))
        points (calculate-points factor (months-since end-date) (months-since start-date))]
    (merge skill {:start-date start-date
                  :end-date   end-date
                  :points     points})))

(defn extract-skill-from-single [experience]
  (let [skills (:skills experience)
        default-start-date (to-date-time (:start-date experience))
        default-end-date (or (to-date-time (:end-date experience)) (now))]
    (map #(update-skill % default-start-date default-end-date) skills))
  )

(defn merge-skills [skills]
  (group-by :name (flatten skills)))

(defn merge-values [map1 map2]
  (let [start-date-1 (:start-date map1)
        start-date-2 (:start-date map2)
        start-date (if (time/before? start-date-1 start-date-2) start-date-1 start-date-2)
        end-date-1 (:end-date map1)
        end-date-2 (:end-date map2)
        end-date (if (time/after? end-date-1 end-date-2) end-date-1 end-date-2)]
    {:name          (:name map1)
     :detailed-name (:detailed-name map1)
     :start-date    start-date
     :end-date      end-date
     :points        (+ (:points map1) (:points map2))
     :category      (:category map1)}))

(defn sum-points [skill]
  (reduce merge-values skill))

(defn calculate-rank [skills]
  (map #(vector (first %) (sum-points (last %))) (seq skills))
  )

(defn compare-points [skill1 skill2]
  (compare (:points (last skill2)) (:points (last skill1))))

(defn compare-end-date [skill1 skill2]
  (compare (:end-date (last skill2)) (:end-date (last skill1))))

(defn compare-skills [skill1 skill2]
  (let [result (compare-end-date skill1 skill2)]
    (if (= 0 result) (compare-points skill1 skill2) result)))

(defn extract-skills [cv]
  (sort compare-skills (calculate-rank (merge-skills (map extract-skill-from-single (:experience cv))))))

(defn group-by-category [skill]
  (group-by #(:category (last %)) skill))

(defn to-category-map [category]
  {:name   (string/capitalize (first category))
   :skills (map #(last %) (last category))})

(defn display? [skill skills-to-display]
  (contains? skills-to-display (first skill)))

(defn filter-skills [skills skills-to-display]
  (filter #(display? % skills-to-display) skills))

(defn compare-names [name1 name2]
  (compare (:name name1) (:name name2)))

(defn skills-grouped-by-categories [cv filter]
  (sort compare-names (map to-category-map (seq (group-by-category (filter-skills (extract-skills cv) filter))))))

(defn add-achievements-empty-list-marker [experience]
  (merge experience {:achievements? (not (empty? (:achievements experience)))})
  )

(defn convert-to-data-map [cv filter]
  {:name            (str (:first-name cv) " " (:sur-name cv))
   :contact         (:contact cv)
   :summary         (:summary cv)
   :availability    (:availability cv)
   :skills          (:skills cv)
   :categories      (skills-grouped-by-categories cv filter)
   :experience      (map add-achievements-empty-list-marker (:experience cv))
   :recommendations (:recommendations cv)
   :learning        (:learning cv)
   :last-updated    (. (DateTimeFormat/shortDate) print (new DateTime))})
