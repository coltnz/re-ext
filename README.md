# re-ext

A Clojure library to provide regular expression utilities in particular designed to allow composing regexs.

## Usage
```clojure
(re/and #"a" #"b") ;compiled at macro time 
=> #"(ab)"
```

```clojure
(re/or #"a" (re-pattern "b"))  ;compiled at run time
=> #"(a|b)a"
```

```clojure
(re/or #"a" (re/and #"b")) ;compiled at macro time
 => #"(a|(b))"
```

```clojure
(def def-re #"bc") ;compiled at run time
(let [local-re #"ef"]
  (re/or def-re local-re))
=> #"(bcd|bcd)"
```

## License

Copyright © 2017 Colin Taylor & Contributors

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

