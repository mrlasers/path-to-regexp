(ns tools.format
  (:require
   [cljfmt.main :as cljfmt]))

(defn -main [& args]
  (let [command (keyword (or (first args) "check"))
        paths ["src" "test" "scripts"]
        options (cljfmt/merge-default-options
                  (read-string (slurp ".cljfmt.clj")))]
    (when (= command :check) (cljfmt/check paths options))
    (when (= command :fix) (cljfmt/fix paths options))))
