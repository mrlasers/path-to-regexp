# path-to-regexp

![Test](https://github.com/maksimr/path-to-regexp/workflows/Test/badge.svg)

Library provided pattern matching for paths

## Installation

```clojure
io.github.maksimr/path-to-regexp {:git/sha "ce679995a01366f9e99cb18df8f7ca4d927a89a8"}
```

## Usage

```clojure
(require '[path-to-regexp.core :refer [match-path]])
(match-path "/user/:id" "/user/1")

=> {:id "1"}
```
