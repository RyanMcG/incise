(ns incise.parsers.helpers
  (:require (clj-time [core :as tm]
                      [coerce :as tc])
            [clojure.string :as s])
  (:import [java.io File]))

(defn dashify
  "Dashify a title, replacing all non word characters with a dash."
  [^String title]
  (-> title
      (s/lower-case)
      (s/replace #"[^\w]" "-")))

(defn date-time->path
  "Convert a DateTime to a path like string."
  [date-time]
  (str (when date-time
         (str (tm/year date-time) \/
              (tm/month date-time) \/
              (tm/day date-time) \/))))

(def date-str->path (comp date-time->path tc/from-string))

(defn Parse->path
  [^Parse {:keys [path date title extension]}]
  (or path
      (str (date-str->path date)
           (dashify title)
           extension)))

(defn extension [^File file]
  "Get the extension, enforcing lower case, on the given file."
  (-> file
      (.getName)
      (s/split #"\.")
      (last)
      (s/lower-case)))
