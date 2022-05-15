(ns path-to-regexp.core-test
  (:require [clojure.test :as t]
            [path-to-regexp.core :refer [match-path path-to-regexp]]))

(t/deftest match-path-test
  (t/are [path-template path params] (= (match-path path-template path) params)
    "/foo/:id" "/foo/1" {:id "1"}
    "/foo/:id" "/foo" nil
    "/foo/:id?" "/foo" {:id nil}))

(t/deftest path-to-regexp-test
  (t/is (= ((path-to-regexp "/foo/:id/bar/:barId?") :params)
          [{:name "id" :optional false}, {:name "barId" :optional true}]))
  (t/is (= ((path-to-regexp "/foo/bar") :params) []))
  (t/are [path-template path params] (= (re-matches (:regexp (path-to-regexp path-template)) path) params)
    "/foo/:id" "/foo/" nil
    "/foo/:id" "/foo/123" ["/foo/123" "123"]
    "/foo/:id" "/foo/123?bar" ["/foo/123?bar" "123"]
    "/foo/:id" "/foo/123#bar" ["/foo/123#bar" "123"]
    "/foo/:id" "bar/foo/123" nil
    "/foo/:id" "/foo/123/bar" nil
    "/foo/:id" "/foo/123?bar" ["/foo/123?bar" "123"]
    "/foo/:id?" "/foo/" ["/foo/" nil]
    "/foo/:id?" "/foo?bar" ["/foo?bar" nil]
    "/foo/:id?" "/foo#bar" ["/foo#bar" nil]
    "/foo/:id?" "/foo/123" ["/foo/123" "123"]
    "/foo/:id?" "/bar" nil
    "/foo/:id*" "/foo/123/bar" ["/foo/123/bar" "123/bar"]))