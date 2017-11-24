# re-ext

A Clojure library to provide regular expression utilities in particular designed to allow composing regexs.

## Usage
```clojure
(re/and #"a" #"b") 
=> #"(ab)"
```

```clojure
(re/or #"a" (re-pattern "b"))
=> #"(a|b)a"
```

```clojure
(re/or #"a" (re/and #"b"))
 => #"(a|(b))"
```

```clojure
(def def-re #"bc")
(let [local-re #"ef"]
  (re/or def-re local-re))
=> #"(bcd|bcd)"
```

## License

Copyright © 2017 Colin Taylor & Contributors

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

