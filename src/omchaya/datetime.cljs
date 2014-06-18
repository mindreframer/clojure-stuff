(ns omchaya.datetime
  (:require [goog.i18n.DateTimeFormat.Format :as date-formats]))

(def full-date-format
  (goog.i18n.DateTimeFormat. date-formats/FULL_DATE))

(def full-datetime-format
  (goog.i18n.DateTimeFormat. date-formats/FULL_DATETIME))

(def full-time-format
  (goog.i18n.DateTimeFormat. date-formats/FULL_TIME))

(def long-date-format
  (goog.i18n.DateTimeFormat. date-formats/LONG_DATE))

(def long-datetime-format
  (goog.i18n.DateTimeFormat. date-formats/LONG_DATETIME))

(def long-time-format
  (goog.i18n.DateTimeFormat. date-formats/LONG_TIME))

(def medium-date-format
  (goog.i18n.DateTimeFormat. date-formats/MEDIUM_DATE))

(def medium-datetime-format
  (goog.i18n.DateTimeFormat. date-formats/MEDIUM_DATETIME))

(def medium-time-format
  (goog.i18n.DateTimeFormat. date-formats/MEDIUM_TIME))

(def short-date-format
  (goog.i18n.DateTimeFormat. date-formats/SHORT_DATE))

(def short-datetime-format
  (goog.i18n.DateTimeFormat. date-formats/SHORT_DATETIME))

(def short-time-format
  (goog.i18n.DateTimeFormat. date-formats/SHORT_TIME))

(defn format-date [date-format date]
  (.format date-format (js/Date. date)))

(def full-date
  (partial format-date full-date-format))

(def full-datetime-format
  (partial format-date full-datetime-format))

(def full-time
  (partial format-date full-time-format))

(def long-date
  (partial format-date long-date-format))

(def long-datetime
  (partial format-date long-datetime-format))

(def long-time
  (partial format-date long-time-format))

(def medium-date
  (partial format-date medium-date-format))

(def medium-datetime
  (partial format-date medium-datetime-format))

(def medium-time
  (partial format-date medium-time-format))

(def short-date
  (partial format-date short-date-format))

(def short-datetime
  (partial format-date short-datetime-format))

(def short-time
  (partial format-date short-time-format))

(def medium-consistent-date-format
  (goog.i18n.DateTimeFormat. "MMM dd, yyyy"))

(def medium-consistent-date
  (partial format-date medium-consistent-date-format))

(def calendar-date-format
  (goog.i18n.DateTimeFormat. "EEE, MMM dd, yyyy 'at' hh:mma"))

(def calendar-date
  (partial format-date calendar-date-format))

(defn date-in-ms [date]
  (let [[y m d] (map js/parseInt (.split (name date) #"-"))]
    (.getTime (js/Date. (js/Date.UTC y (dec m) (dec d) 0 0 0)))))

(def day-in-ms
  (* 1000 3600 24))

                                        ; Units of time in seconds
(def minute
  60)

(def hour
  (* minute 60))

(def day
  (* hour 24))

(def month
  (* day 30))

(def year
  (* month 12))

(defn time-ago [time]
  (let [now (.getTime (js/Date.))
        ago (.floor js/Math (/ (- now time) 1000))
        interval (cond (< ago hour)  {:divisor minute :unit "minute" }
                       (< ago day)   {:divisor hour   :unit "hour"   }
                       (< ago month) {:divisor day    :unit "day"    }
                       (< ago year)  {:divisor month  :unit "month"  }
                       :else         {:divisor year   :unit "year"   })]
    (let [count (.round js/Math (/ ago (:divisor interval)))]
      (str count " "  (:unit interval) (when-not (= 1 count) "s")))))
