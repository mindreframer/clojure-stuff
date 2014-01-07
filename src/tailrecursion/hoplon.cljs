;; Copyright (c) Alan Dipert and Micha Niskin. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns tailrecursion.hoplon
  (:require-macros
    [tailrecursion.javelin :refer [with-let cell=]])
  (:require
    [tailrecursion.javelin :refer [lift]] 
    [goog.dom         :as gdom]
    [cljs.reader      :refer [read-string]]
    [clojure.string   :refer [split join blank?]]))

(declare do! on! $text)

(def is-ie8 (not (aget js/window "Node")))

(def node?
  (if-not is-ie8
    #(instance? js/Node %)
    #(try (.-nodeType %) (catch js/Error _))))

(def vector?*
  (if-not is-ie8
    vector?
    #(try (vector? %) (catch js/Error _))))

(def seq?*
  (if-not is-ie8
    seq?
    #(try (seq? %) (catch js/Error _))))

(set-print-fn!
  #(when (and js/console (.-log js/console)) (.log js/console %)))

(defn safe-nth
  ([coll index] (safe-nth coll index nil))
  ([coll index not-found]
   (try (nth coll index not-found) (catch js/Error _ not-found))))

(defn pad-seq [n coll & [x]] 
  (let [p (repeat n x)]
    (let [z (- n (count coll))]
      (if (pos? z) (concat coll (take z p)) coll))))

(defn timeout
  ([f] (timeout f 0))
  ([f t] (.setTimeout js/window f t)))

;; env ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn unsplice [forms]
  (mapcat #(if (or (seq?* %) (vector?* %)) (unsplice %) [%]) forms))

(defn when-dom [this f]
  (timeout
    (fn doit []
      (if (.contains (.-documentElement js/document) this) (f) (timeout doit 20)))))

(defn parse-args [[head & tail :as args]]
  (let [kw1? (comp keyword? first)
        mkkw #(->> (partition 2 %) (take-while kw1?) (map vec))
        drkw #(->> (partition 2 2 [] %) (drop-while kw1?) (mapcat identity))]
    (cond (map?     head) [head tail]
          (keyword? head) [(into {} (mkkw args)) (drkw args)]
          :else           [{} args])))

(defn add-attributes! [this attr]
  (let [prefix #(.substr % 0 3)
        suffix #(keyword (.substr % 3))
        dos    (atom {}) 
        ons    (atom {})
        addcls #(join " " (-> %1 (split #" ") set (into (split %2 #" "))))]
    (doseq [[k v] attr]
      (let [k (name k), e (js/jQuery this)]
        (cond (= k "class")         (doseq [cls (split v #" ")] (.addClass e cls))
              (= k "css")           (.css e (clj->js v))
              (= "do-" (prefix k))  (swap! dos assoc (suffix k) v)
              (= "on-" (prefix k))  (swap! ons assoc (suffix k) v)
              :else                 (cond (= false v) (.removeAttr e k)
                                          (= true v)  (.attr e k k)
                                          :else       (.attr e k (str v))))))
    (when (seq @ons)
      (timeout (fn [] (doseq [[k v] @ons] (on! this k v))))) 
    (when (seq @dos)
      (timeout (fn [] (doseq [[k v] @dos] (do! this k @v) (add-watch v (gensym) #(do! this k %4))))))
    this))

(def append-child
  (if-not is-ie8
    #(.appendChild %1 %2)
    #(try (.appendChild %1 %2) (catch js/Error _))))

(defn add-children! [this kids]
  (let [node #(cond (string? %) ($text %) (node? %) %)]
    (doseq [x (keep node (unsplice kids))] (append-child this x))
    this))

(defn on-append! [this f]
  (set! (.-hoplonIFn this) f))

(extend-type js/Element
  IPrintWithWriter
  (-pr-writer
    ([this writer opts]
     (write-all writer "#<Element: " (.-tagName this) ">")))
  IFn
  (-invoke
    ([this & args]
     (let [[attr kids] (parse-args args)]
       (if (.-hoplonIFn this)
         (doto this (.hoplonIFn attr kids))
         (doto this (add-attributes! attr) (add-children! kids)))))))

(defn- make-elem-ctor [tag]
  (fn [& args]
    (apply (.createElement js/document tag) args)))

(def a              (make-elem-ctor "a"))
(def abbr           (make-elem-ctor "abbr"))
(def acronym        (make-elem-ctor "acronym"))
(def address        (make-elem-ctor "address"))
(def applet         (make-elem-ctor "applet"))
(def area           (make-elem-ctor "area"))
(def article        (make-elem-ctor "article"))
(def aside          (make-elem-ctor "aside"))
(def audio          (make-elem-ctor "audio"))
(def b              (make-elem-ctor "b"))
(def base           (make-elem-ctor "base"))
(def basefont       (make-elem-ctor "basefont"))
(def bdi            (make-elem-ctor "bdi"))
(def bdo            (make-elem-ctor "bdo"))
(def big            (make-elem-ctor "big"))
(def blockquote     (make-elem-ctor "blockquote"))
(def body           (make-elem-ctor "body"))
(def br             (make-elem-ctor "br"))
(def button         (make-elem-ctor "button"))
(def canvas         (make-elem-ctor "canvas"))
(def caption        (make-elem-ctor "caption"))
(def center         (make-elem-ctor "center"))
(def cite           (make-elem-ctor "cite"))
(def code           (make-elem-ctor "code"))
(def col            (make-elem-ctor "col"))
(def colgroup       (make-elem-ctor "colgroup"))
(def command        (make-elem-ctor "command"))
(def data           (make-elem-ctor "data"))
(def datalist       (make-elem-ctor "datalist"))
(def dd             (make-elem-ctor "dd"))
(def del            (make-elem-ctor "del"))
(def details        (make-elem-ctor "details"))
(def dfn            (make-elem-ctor "dfn"))
(def dir            (make-elem-ctor "dir"))
(def div            (make-elem-ctor "div"))
(def dl             (make-elem-ctor "dl"))
(def dt             (make-elem-ctor "dt"))
(def em             (make-elem-ctor "em"))
(def embed          (make-elem-ctor "embed"))
(def eventsource    (make-elem-ctor "eventsource"))
(def fieldset       (make-elem-ctor "fieldset"))
(def figcaption     (make-elem-ctor "figcaption"))
(def figure         (make-elem-ctor "figure"))
(def font           (make-elem-ctor "font"))
(def footer         (make-elem-ctor "footer"))
(def form           (make-elem-ctor "form"))
(def frame          (make-elem-ctor "frame"))
(def frameset       (make-elem-ctor "frameset"))
(def h1             (make-elem-ctor "h1"))
(def h2             (make-elem-ctor "h2"))
(def h3             (make-elem-ctor "h3"))
(def h4             (make-elem-ctor "h4"))
(def h5             (make-elem-ctor "h5"))
(def h6             (make-elem-ctor "h6"))
(def head           (make-elem-ctor "head"))
(def header         (make-elem-ctor "header"))
(def hgroup         (make-elem-ctor "hgroup"))
(def hr             (make-elem-ctor "hr"))
(def html           (make-elem-ctor "html"))
(def i              (make-elem-ctor "i"))
(def iframe         (make-elem-ctor "iframe"))
(def img            (make-elem-ctor "img"))
(def input          (make-elem-ctor "input"))
(def ins            (make-elem-ctor "ins"))
(def isindex        (make-elem-ctor "isindex"))
(def kbd            (make-elem-ctor "kbd"))
(def keygen         (make-elem-ctor "keygen"))
(def label          (make-elem-ctor "label"))
(def legend         (make-elem-ctor "legend"))
(def li             (make-elem-ctor "li"))
(def link           (make-elem-ctor "link"))
(def html-map       (make-elem-ctor "map"))
(def mark           (make-elem-ctor "mark"))
(def menu           (make-elem-ctor "menu"))
(def html-meta      (make-elem-ctor "meta"))
(def meter          (make-elem-ctor "meter"))
(def nav            (make-elem-ctor "nav"))
(def noframes       (make-elem-ctor "noframes"))
(def noscript       (make-elem-ctor "noscript"))
(def object         (make-elem-ctor "object"))
(def ol             (make-elem-ctor "ol"))
(def optgroup       (make-elem-ctor "optgroup"))
(def option         (make-elem-ctor "option"))
(def output         (make-elem-ctor "output"))
(def p              (make-elem-ctor "p"))
(def param          (make-elem-ctor "param"))
(def pre            (make-elem-ctor "pre"))
(def progress       (make-elem-ctor "progress"))
(def q              (make-elem-ctor "q"))
(def rp             (make-elem-ctor "rp"))
(def rt             (make-elem-ctor "rt"))
(def ruby           (make-elem-ctor "ruby"))
(def s              (make-elem-ctor "s"))
(def samp           (make-elem-ctor "samp"))
(def script         (make-elem-ctor "script"))
(def section        (make-elem-ctor "section"))
(def select         (make-elem-ctor "select"))
(def small          (make-elem-ctor "small"))
(def source         (make-elem-ctor "source"))
(def span           (make-elem-ctor "span"))
(def strike         (make-elem-ctor "strike"))
(def strong         (make-elem-ctor "strong"))
(def style          (make-elem-ctor "style"))
(def sub            (make-elem-ctor "sub"))
(def summary        (make-elem-ctor "summary"))
(def sup            (make-elem-ctor "sup"))
(def table          (make-elem-ctor "table"))
(def tbody          (make-elem-ctor "tbody"))
(def td             (make-elem-ctor "td"))
(def textarea       (make-elem-ctor "textarea"))
(def tfoot          (make-elem-ctor "tfoot"))
(def th             (make-elem-ctor "th"))
(def thead          (make-elem-ctor "thead"))
(def html-time      (make-elem-ctor "time"))
(def title          (make-elem-ctor "title"))
(def tr             (make-elem-ctor "tr"))
(def track          (make-elem-ctor "track"))
(def tt             (make-elem-ctor "tt"))
(def u              (make-elem-ctor "u"))
(def ul             (make-elem-ctor "ul"))
(def html-var       (make-elem-ctor "var"))
(def video          (make-elem-ctor "video"))
(def wbr            (make-elem-ctor "wbr"))

(def spliced        vector)
(def $text          #(.createTextNode js/document %))
(def $comment       #(.createComment js/document %))

(def *initfns*    (atom []))
(def add-initfn!  (partial swap! *initfns* conj))

(defn init [html]
  (timeout
    (fn []
      (let [body (js/jQuery "body")]
        (.empty body)
        (doseq [x html] (.append body x))
        (-> body (.on "submit" (fn [e] (.preventDefault e))))
        (doseq [f @*initfns*] (f))))))

;; frp ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn by-id [id] (.getElementById js/document (name id)))

(defn val-id [id] (do! (by-id id) :value))

(defn rel [other event]
  (let [os (js->clj (.offset (js/jQuery other))) 
        ox (os "left")
        oy (os "top")]
    {:x (- (.-pageX event) ox) :y (- (.-pageY event) oy)}))

(defn relx [other event] (:x (rel other event)))

(defn rely [other event] (:y (rel other event)))

(defn rel-event [rel-to tag handler]
  (fn [event]
    (aset event (str tag "X") (relx rel-to event))
    (aset event (str tag "Y") (rely rel-to event))
    (handler event)))

(defn text-val!
  ([e] (.val e))
  ([e v] (-> e (.val (str v)) (.trigger "change"))))

(defn check-val!
  ([e] (.is e ":checked"))
  ([e v] (-> e (.prop "checked" (boolean v)) (.trigger "change"))))

(defmulti do! (fn [elem action & args] action))

(defmethod do! :value
  [elem _ & args] 
  (let [e (js/jQuery elem)]
    (apply (if (= "checkbox" (.attr e "type")) check-val! text-val!) e args)))

(defmethod do! :attr
  [elem _ kvs]
  (elem kvs))

(defmethod do! :class
  [elem _ kvs] 
  (let [elem (js/jQuery elem)]
    (doseq [[c p?] kvs]
      (.toggleClass elem (name c) (boolean p?)))))

(defmethod do! :css
  [elem _ kvs]
  (let [e (js/jQuery elem)]
    (doseq [[k v] kvs] (.css e (name k) (str v)))))

(defmethod do! :toggle
  [elem _ v]
  (.toggle (js/jQuery elem) (boolean v)))

(defmethod do! :slide-toggle
  [elem _ v]
  (if v
    (.slideDown (.hide (js/jQuery elem)) "fast")
    (.slideUp (js/jQuery elem) "fast")))

(defmethod do! :fade-toggle
  [elem _ v]
  (if v
    (.fadeIn (.hide (js/jQuery elem)) "fast")
    (.fadeOut (js/jQuery elem) "fast")))

(defmethod do! :focus
  [elem _ v]
  (if v
    (timeout (fn [] (.focus (js/jQuery elem))))
    (timeout (fn [] (.focusout (js/jQuery elem))))))

(defmethod do! :select
  [elem _ _]
  (.select (js/jQuery elem)))

(defmethod do! :focus-select
  [elem _ v]
  (when v (do! elem :focus v) (do! elem :select v)))

(defmethod do! :text
  [elem _ v]
  (.text (js/jQuery elem) (str v)))

(defmethod do! :scroll-to
  [elem _ v]
  (when v
    (let [body (js/jQuery "body")
          elem (js/jQuery elem)]
      (.animate body (clj->js {:scrollTop (.-top (.offset elem))})))))

(defn on! [elem event callback]
  (when-dom elem #(.on (js/jQuery elem) (name event) callback)))
