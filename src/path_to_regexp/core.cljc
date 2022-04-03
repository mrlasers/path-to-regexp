(ns path-to-regexp.core
  (:require [clojure.string :as string]))

(defn path-to-regexp
  "Convert passed path pattern to RegExp.
  Returns map with RegExp and path parameters"
  [path]
  (let [params (atom [])
        pattern (re-pattern
                  (str "^"
                    (-> (string/replace path #"([().])" (fn [[_ c]] (str "\\" c)))
                      (string/replace #"(\/)?:(\w+)(\*\?|[?*])?"
                        (fn [[_ slash param-name option]]
                          (let [optional (or (= option "?") (= option "*?"))
                                star (or (= option "*") (= option "*?"))]
                            (swap! params conj
                              {:name param-name
                               :optional optional})
                            (str
                              (if optional (str "(?:" slash) (str slash "(?:"))
                              (if star "([^#?]+?)" "([^/#?]+)")
                              (if optional "?)?" ")")))))
                      (str "(?:[?#].*|$)"))))]
    {:params @params
     :regexp pattern}))

(defn exec-path
  "Match path pattern agains path.
  Returns map with matched path parameters or nil if path does not match pattern"
  [parsed-path path]
  (let [matches (re-matches (:regexp parsed-path) path)]
    (if-not matches
      nil
      (reduce (fn [params [param-name value]]
                (assoc params param-name value))
        {}
        (map (fn [value param] [(keyword (:name param)) value]) (rest matches) (:params parsed-path))))))

(defn match-path
  "Match path against passed pattern and returns nil if path does not match pattern
  or map with matched path parameters"
  [path-pattern path]
  (let [parsed-path (path-to-regexp path-pattern)]
    (exec-path parsed-path path)))
