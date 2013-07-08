#### CHP [![endorse](https://api.coderwall.com/runexec/endorsecount.png)](https://coderwall.com/runexec)
ClojureHomePage is a Compojure based web framework that allows you to write the backend and frontend with Clojure.

You can <br />
* Embed Clojure into a HTML file with the ```<clojure></clojure>``` tags
* Enable multiple method handlers under a single route (get, post, put, delete, and head)

This page serves as project documentation.<br />

1. [Tutorial](https://github.com/runexec/chp/tree/master/tutorial/01)
2. [Install](#getting-started)
3. [How?](#how)
4. [Example CHTML & Routes](#example-chtml--routes)
5. [Clojure HTML Generation](#clojure-and-html-generation)
6. [Clojure and CSS Generation](#clojure-and-css-generation)
7. [Clojure and JavaScript Generation](#clojure-and-javascript-generation)
8. [Clojure and SQL](#clojure-and-sql)

#### Getting started

1) Download & Run

```bash
git clone https://github.com/runexec/chp
cd chp
lein ring server
```

2) Edit the default app-routes template located at the bottom of src/chp/handler.clj

#### How?

By default, the CHTML files are located in chp-root folder of the project folder.
When a CHTML file is parsed, all public variables of the chp.handler namespace
are accessible during the evaluation of the ```<clojure></clojure>``` tags. Use print
 or println within the tags to have the results displayed.


#### Example CHTML & Routes

The following link is the chtml page that is used in the example below. 
<a href="https://github.com/runexec/chp/blob/master/chp-root/test-page.chtml">
   test-page.chtml
</a>


```clojure
(defroutes app-routes
  (chp-route "/chtml" 
  	     ;; access dynamic variables in chp.handler namespace
             (binding [*title* "Test Page Example"]
               (or (chp-parse (str root-path "test-page.chtml"))
                   "error")))
  (chp-route "/"
             (let [display (str "Method " ($ method) "<br />"
                                "URI " ($ uri) "<br />"
                                "Params " ($ params) "<br />")]
               (chp-body {:-get (str "Get => " display)
                          :-post (str "Post => " display)
                          :-not-found "Sorry, but this page doesn't exist"})))
  (chp-route "/testing"
             (or (chp-when :get
                           (str "chp-body wasn't used to access "
                                ($ uri)))
                 "Not Found"))
  (chp-route "/chp"
             (or (chp-parse (str root-path "chp-info.chtml"))
                 "error"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
```


#### Clojure and HTML Generation

The following methods presented in the documentation below are 
accessible from within CHTML files by default. These abstractions
are drop-in replacements for the Hiccup API located at http://weavejester.github.io/hiccup/.
Please note that these forms DO NOT generate Hiccup code, but HTML.


```clojure
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Form Fields

(escape string)

(url-encode string)

(check-box attr-map? name)
(check-box attr-map? name checked?)
(check-box attr-map? name checked? value)

(drop-down attr-map? name options)
(drop-down attr-map? name options selected)

(email-field attr-map? name)
(email-field attr-map? name value)

(file-upload attr-map? name)

(form-to attr-map? [method action] & body)

(hidden-field attr-map? name)
(hidden-field attr-map? name value)

(label attr-map? name text)

(password-field attr-map? name)
(password-field attr-map? name value)

(radio-button attr-map? group)
(radio-button attr-map? group checked?)
(radio-button attr-map? group checked? value)

(reset-button attr-map? text)

(select-options attr-map? coll)
(select-options attr-map? coll selected)

(submit-button attr-map? text)

(text-area attr-map? name)
(text-area attr-map? name value)

(text-field attr-map? name)
(text-field attr-map? name value)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Common Elements

(javascript-tag script)

(image attr-map? src)
(image attr-map? src alt)

(link-to attr-map? url & content)

(mail-to attr-map? e-mail & [content])

(ordered-list attr-map? coll)

(unordered-list attr-map? coll)

```

#### Clojure and CSS Generation

ClojureHomePage uses the Garden CSS generation library by default.
The Garden documentation page is located at https://github.com/noprompt/garden
<p>
CHP CSS wrapper is located in the namespace chp.css and is loaded by default.
</p>

#### Clojure and JavaScript Generation

ClojureHomePage uses ClojureScript and lein-cljsbuild to generate javascript.
CHP uses the directory resources/cljs/ as the default cljs source code directory.

1. [lein-cljsbuild Documentation](https://github.com/emezeske/lein-cljsbuild/)
2. [ClojureScript Documentation](https://github.com/clojure/clojurescript)

#### Clojure and SQL 

ClojureHomePage uses the SQLKorma DSL by default. korma.db is required as kdb and korma.core is required as kc

1. [Korma Documentation](http://www.sqlkorma.com/)
