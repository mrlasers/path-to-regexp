(ns path-to-regexp.core
  (:require [clojure.string :as string]))

(defn path-to-regexp
  "Convert a passed path pattern to RegExp.
  Returns map with RegExp and path parameters"
  [path]
  (let [params* (transient [])
        pattern (re-pattern
                  (str "^"
                    (->
                      (string/replace path #"([().])" (fn [[_ c]] (str "\\" c)))
                      (string/replace #"(\/)?:(\w+)(\*\?|[?*])?"
                        (fn [[_ slash param-name option]]
                          (let [optional (or (= option "?") (= option "*?"))
                                star (or (= option "*") (= option "*?"))]
                            (conj! params*
                              {:name param-name
                               :optional optional})
                            (str
                              (if optional (str "(?:" slash) (str slash "(?:"))
                              (if star "([^#?]+?)" "([^/#?]+)")
                              (if optional "?)?" ")")))))
                      (str "(?:[?#].*|$)"))))]
    {:params (persistent! params*)
     :regexp pattern}))

(defn exec-path
  "Match a path pattern again path.
  Returns map with matched path parameters or nil if a path does not match the pattern"
  [parsed-path path]
  (let [matches (re-matches (:regexp parsed-path) path)]
    (if-not matches
      nil
      (reduce (fn [params [param-name value]]
                (assoc params param-name value))
        {}
        (map (fn [value param] [(keyword (:name param)) value]) (rest matches) (:params parsed-path))))))

(defn match-path
  "Match a path against a passed pattern and returns nil if the path does not match the pattern
  or map with matched path parameters"
  [path-pattern path]
  (let [parsed-path (path-to-regexp path-pattern)]
    (exec-path parsed-path path)))