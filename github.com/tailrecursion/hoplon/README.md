<img src="img/Hoplite.jpg">

# Hoplon

Hoplon is a set of tools and libraries for making web applications. Hoplon
provides a compiler for web application frontend development, and includes
the following libraries as dependencies to complete the stack:

* [Javelin][1]: a spreadsheet-like dataflow library for managing client
  state. Hoplon tightly integrates with Javelin to reactively bind DOM
  elements to the underlying Javelin cell graph.
* [Castra][2]: a full-featured RPC library for Clojure and
  ClojureScript, providing the serverside environment.
* [Cljson][3]: an efficient method for transferring Clojure/ClojureScript
  data between client and server. Castra uses cljson as the underlying
  transport protocol.

### Example

```xml
<script type="text/hoplon">
  ;; Page declaration specifies output file path.
  (page "index.html")
  
  ;; definitions are optional
  (defn my-list [& items]
    (div
      :class "my-list"
      (apply ul (map #(li (div :class "my-list-item" %)) items))))

  (def clicks (cell 0))
</script>
    
<html>
  <head>
    <title>example page</title>
  </head>
  <body>
    <h1>Hello, Hoplon</h1>
    
    <!-- an HTML syntax call to the my-list function -->
    <my-list>
      <span>first thing</span>
      <span>second thing</span>
    </my-list>

    <!-- using FRP to link DOM and Javelin cells -->
    <p><text>You've clicked ~{clicks} times, so far.</text></p>
    <button on-click="{{ #(swap! clicks inc) }}">click me</button>
  </body>
</html>
```

Or, equivalently:

```clojure
(page "index.html")

(defn my-list [& items]
  (div
    :class "my-list"
    (apply ul (map #(li (div :class "my-list-item" %)) items))))

(def clicks (cell 0))

(html
  (head
    (title "example page"))
  (body
    (h1 "Hello, Hoplon")

    (my-list
      (span "first thing")
      (span "second thing"))

    (p (text "You've clicked ~{clicks} times, so far."))
    (button :on-click #(swap! clicks inc) "click me")))
```

### Documentation

* [http://hoplon.io][7]
* [Design Document][6] (early version)

### Demos

* [Hoplon demo applications repository][5]

### Dependency

Artifacts are published on [Clojars][4]. 

![latest version][8]

## License

```
Copyright (c) Alan Dipert and Micha Niskin. All rights reserved.

The use and distribution terms for this software are covered by the Eclipse
Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
be found in the file epl-v10.html at the root of this distribution. By using
this software in any fashion, you are agreeing to be bound by the terms of
this license. You must not remove this notice, or any other, from this software.
```

[1]: https://github.com/tailrecursion/javelin
[2]: https://github.com/tailrecursion/castra
[3]: https://github.com/tailrecursion/cljson
[4]: https://clojars.org/tailrecursion/hoplon
[5]: https://github.com/tailrecursion/hoplon-demos
[6]: doc/Design.md
[7]: http://hoplon.io/
[8]: https://clojars.org/tailrecursion/hoplon/latest-version.svg
