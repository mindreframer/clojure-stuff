(ns plumbing.core-test
  (:use clojure.test plumbing.core)
  (:require
   [schema.core :as s]
   [schema.macros :as sm]
   [schema.test :as schema-test]
   [plumbing.fnk.pfnk :as pfnk]
   [plumbing.fnk.impl :as fnk-impl]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Maps

(deftest for-map-test
  (is (= (for-map [i [1 2]
                   j [10 20]]
           (+ i j)
           j)
         {11 10 12 10
          21 20 22 20}))
  (is (= (for-map [i [1 2]
                   j [10 20]]
           i
           j)
         {1 20 2 20}))
  (let [m (for-map m [i (range 1000)] i (+ i (get m (dec i) 0)))]
    (is (= (count m) 1000))
    (is (= (m 999) 499500))))

(deftest map-vals-test
  (is (= (map-vals inc {:a 0 :b 0})
         {:a 1 :b 1}))
  (is (= (map-vals inc [[:a 0] [:b 0]])
         {:a 1 :b 1}))
  (is (= (map-vals inc (sorted-map :a 0 :b 0))
         {:a 1 :b 1}))
  (is (sorted? (map-vals inc (sorted-map :a 0 :b 0)))))

(deftest map-keys-test
  (is (= (map-keys str {:a 1 :b 1})
         {":a" 1 ":b" 1}))
  (is (= (map-keys str [[:a 1] [:b 1]])
         {":a" 1 ":b" 1})))

(deftest map-from-keys-test
  (is (= (map-from-keys inc [0 1 2])
         {0 1, 1 2, 2 3})))

(deftest map-from-vals-test
  (is (= (map-from-vals inc [0 1 2])
         {1 0, 2 1, 3 2})))

(deftest dissoc-in-test
  (is (= {:a 1} (dissoc-in {:a 1 :b 2} [:b])))
  (is (= {:a 1 :b {:d 3}} (dissoc-in {:a 1 :b {:c 2 :d 3}} [:b :c])))
  (is (= {:a 1} (dissoc-in {:a 1 :b {:c 2}} [:b :c])))
  (is (= {:a 1} (dissoc-in {:a 1} [:b :c])))
  (is (thrown? Exception (dissoc-in {:a 1 :b :not-a-map} [:b :c])))
  (is (= nil (dissoc-in {:a 1} [:a])))
  (is (= nil (dissoc-in nil [:a])))
  (is (= nil (dissoc-in {} [:a]))))

(deftest keywordize-map-test
  (is (= {:foo 1
          :bar [2]
          :baz [{:x 42}]}
         (keywordize-map {"foo" 1
                          "bar" [2]
                          :baz [{"x" 42}]})))

  (is (= {:foo 1
          :bar [2]
          :baz {:x 42}}
         (keywordize-map {"foo" 1
                          "bar" [2]
                          :baz {"x" 42}}))))

(deftest lazy-get-test
  (let [counter (atom 0)]
    (is (= 1 (lazy-get {:a 1} :a (do (swap! counter inc) 2))))
    (is (zero? @counter))
    (is (= 2 (lazy-get {:a 1} :b (do (swap! counter inc) 2))))
    (is (= 1 @counter))
    (is (= 2 (lazy-get {:a 1 :b 2} :b (do (swap! counter inc) 2))))
    (is (= 1 @counter))))


(deftest safe-get-test
  (is (= 2 (safe-get {:a 2} :a)))
  (is (thrown? Exception (safe-get {:a 2} :b)))
  (is (= 2 (safe-get-in {:a {:b 2}} [:a :b])))
  (is (thrown? Exception (safe-get-in {:a {:b 2}} [:b])))
  (is (thrown? Exception (safe-get-in {:a {:b 2}} [:a :c])))
  (is (thrown? Exception (safe-get-in {:a {:b 2}} [:a :b :d]))))

(deftest assoc-when-test
  (is (= {:a 1} (assoc-when nil :a 1)))
  (is (= {:a 1 :c 2} (assoc-when {:a 1} :b nil :c 2))))

(deftest update-in-when-test
  (is (= nil (update-in-when nil [:a] inc)))
  (is (= {:a {:b 2}} (update-in-when {:a {:b 2}} [:a :c] inc)))
  (is (= {} (update-in-when {} [:foo :bar] inc)))
  (is (= {:foo 2 :bar 1})
      (update-in-when {:foo 1 :bar 1} [:foo] inc))
  (is (= {:a {:b 3 :z 5}} (update-in-when {:a {:b 2 :z 5}} [:a :b] inc))))

(deftest grouped-map-test
  (is (= {:a [1 2] :b [3]} (grouped-map first second [[:a 1] [:b 3] [:a 2]]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Seqs

(deftest aconcat-test
  (is (= [1 2 3 4 5 6] (aconcat [[1 2 3] [4 5 6]]))))

(deftest unchunk-test
  (let [realized (atom #{})
        xs (map (fn [x]
                  (swap! realized conj x)
                  x)
                (unchunk (range 10)))]
    (is (empty? @realized))
    (doseq [x (range 10)]
      (is (not (@realized x)))
      (is (= x (nth xs x)))
      (is (@realized x)))))

(deftest sum-test
  (is (= 55 (sum (range 1 11))))
  (is (= 55 (sum inc (range 10)))))

(deftest singleton-test
  (is (= 1 (singleton [1])))
  (is (nil? (singleton [1 2]))))

(deftest indexed-test
  (is (empty? (indexed nil)))
  (is (= [[0 :a] [1 :b] [2 :c]] (indexed [:a :b :c])))
  (is (= [[0 :a] [1 :b] [2 :c] [3 0]] (take 4 (indexed (concat [:a :b :c] (range)))))))

(deftest positions-test
  (is (empty? (positions odd? [2 4 6 8 10])))
  (is (= [0 1 2] (positions odd? [1 3 5 2 4 6])))
  (is (= [1 3 5] (take 3 (positions odd? (range))))))

(deftest frequencies-fast-test
  (is (= {\p 2, \s 4, \i 4, \m 1}
         (frequencies-fast "mississippi")))
  (is (= {1 3 2 2 3 1}
         (frequencies-fast [1 2 3 1 2 1])))
  ;; We don't return the right thing on = but not .equals things,
  ;; because of the difference between Java Maps and Clojure maps.
  (is (= {1 1}
         (frequencies-fast [1 (BigInteger. "1")]))))

(deftest distinct-fast-test
  (is (= [1 2 3]
         (distinct-fast [1 2 3])))
  (is (= [1 2 3]
         (distinct-fast [1 2 3 2 1 2 3 2 2])))
  (is (= []
         (distinct-fast []))))

(defn are-fast-things-faster []
  (let [s (apply concat (repeat 100 (range 10000)))]
    (doseq [f [frequencies frequencies-fast distinct distinct-fast]]
      (println f)
      (dotimes [_ 5]
        (time (doall (f s)))))))

(deftest distinct-by-test
  (is (= [{:id 1 :data "a"}]
         (distinct-by :id
                      [{:id 1 :data "a"}
                       {:id 1 :data "b"}])))
  (is (= [1 2 3 2 1]
         (map second
              (distinct-by
               first
               [[1 1]
                [1 10]
                [17 2]
                [1 12]
                [:foo 3]
                [:foo 3]
                ['bar 2]
                [1 3]
                [3 1]])))))

(deftest distinct-id-test
  (let [x (distinct-id [:a :b :c :a :b (Long. 1) (Long. 1)])]
    (is (= 5 (count x)))
    (is (= #{:a :b :c 1} (set x)))
    (is (= #{:a :b :c 1} (set x)))
    (is (empty? (distinct-id nil)))))

(deftest interleave-all-test
  (is (= [:a 0 :b 1 :c :d] (interleave-all [:a :b :c :d] [0 1]))))

(deftest count-when-test
  (is (= 5 (count-when even? (range 10)))))

(deftest conj-when-test
  (is (= [:a :b :c]
         (conj-when [:a] :b nil :c))))

(deftest cons-when-test
  (is (= [1 2] (cons-when nil [1 2])))
  (is (= [1 2] (cons-when false [1 2])))
  (is (= [3 1 2] (cons-when 3 [1 2]))))

(deftest rsort-by-test
  (is (= [5 4 3 2 1] (rsort-by identity [3 2 1 4 5]))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Control flow

(deftest ?>>-test
  (let [side-effect (atom [])]
    (is (= (range 10)
           (->> (range 10)
                (?>> false (do (swap! side-effect conj :bad) map) inc))))
    (is (empty? @side-effect))
    (is (= (range 1 11)
           (->> (range 10)
                (?>> true (do (swap! side-effect conj :good) map) inc))))
    (is (= @side-effect [:good]))))

(deftest ?>-test
  (let [side-effect (atom [])]
    (is (= {:a 1}
           (-> {:a 1}
               (?> false (do (swap! side-effect conj :bad) assoc) :b 1))))
    (is (empty? @side-effect))
    (is (= {:a 1 :b 1}
           (-> {:a 1}
               (?> true (do (swap! side-effect conj :good) assoc) :b 1))))
    (is (= @side-effect [:good]))))

(deftest fn->-test
  (is (= {:a 1 :b 1} ((fn-> (assoc :a 1)) {:b 1}))))

(deftest fn->>-test
  (is (= (range 1 11)  ((fn->> (map inc)) (range 10)))))

(deftest <--test
  (is (= [2 3]
         (-> {1 1}
             (assoc 3 4)
             (update-in [1] inc)
             (->> (map-vals dec)
                  (map-keys inc)
                  (<- (update-in [2] inc)
                      (map [2 4])))))))

(deftest as->>-test
  (is (= [1 2 3]
         (->> (range 5)
              (map inc)
              (as->> x (drop-last 2 x))))))

(deftest memoized-fn-test
  (let [calls (atom 0)]
    (is (= 55
           ((memoized-fn fib [x] (swap! calls inc)
                         (case x 0 0 1 1 (+ (fib (- x 1)) (fib (- x 2)))))
            10)))
    (is (= 11 @calls))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Miscellaneous

(deftest swap-pair!-test
  (let [a (atom {:a 1})]
    (is (= [{:a 1} {:a 2}] (swap-pair! a #(update-in % [:a] inc)))))
  (let [a (atom {:a 1})]
    (is (= [{:a 1} {:a 2}] (swap-pair! a update-in [:a] inc)))))

(deftest get-and-set!-test
  (let [a (atom 1)]
    (is (= 1 (get-and-set! a 2)))
    (is (= 2 @a))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; fnk

(deftest letk-test
  (let [called? (atom false)
        om {:a 1 :c 3 :d 4 :e 17 :g 22}]
    (letk [[a { b 2} c d {e 5} :as m & more] om]
      (is (= [a b c d e] [1 2 3 4 17]))
      (is (= m om))
      (is (= {:g 22} more))
      (reset! called? true))
    (is (= @called?))
    (letk [[:as m] om]
      (is (= m om)))
    (letk [[a & m] om]
      (is (= a 1))
      (is (= m (dissoc om :a))))
    (letk [[a] {:a {:b 1}}
           [b] a]
      (is (= b 1)))
    (is (thrown? Throwable
                 (letk [[a] {:b 2}] a)))))

(deftest letk-self-shadow-test
  (is (= 2 (let [a 1] (letk [[{a a}] {:a 2}] a))))
  (is (= 1 (let [a 1] (letk [[{a a}] {}] a)))))

(deftest letk-single-shadow-test
  (let [a 1 b 2 c 3 e 4 e 5]
    (is (= [8 8 8 5 10] (letk [[c {a c} {b a} {d e} e] {:c 8 :e 10}] [a b c d e])))
    (is (= [8 8 8 5 10] (letk [[c [:nest {a c} {b a} {d e}] e] {:c 8 :e 10 :nest {}}] [a b c d e])))))

(deftest letk-dont-require-map-for-nested-only-as
  (is (= 1 (letk [[[:a :as a]] {:a 1}] a))))

(deftest letk-no-multiple-binding-test
  (is (thrown? Exception (eval '(letk [[a a] {:a 1}] a))))
  (is (= 1 (letk [[a] {:a 1} [a] {:a a}] a))))

(deftest letk-multi-shadow-test
  (let [a 1 b 2 c 3 e 4 e 5
        inp {:c 8 :e 10}]
    (is (= [8 8 8 5 10] (letk [[c] inp
                               [{a c}] inp
                               [{b a}] inp
                               [{d e}] inp
                               [e] inp]
                          [a b c d e])))))

(deftest fnk-test
  (let [call-count (atom 0)
        om {:a 1 :c 3 :d 4 :e 17 :g 22}]
    ((fnk [a]
       (is (= a 1))
       (swap! call-count inc))
     {:a 1})
    ((fnk [a {b 2} c d {e 5} :as m & more]
       (is (= [a b c d e] [4 2 3 4 17]))
       (is (= m (assoc om :a 4 :h 77)))
       (is (= {:g 22 :h 77} more))
       (swap! call-count inc))
     (assoc om :a 4 :h 77))
    (let [f (fnk [a {b 2} [:c :as c0] [:d d1 {d2 2} [:d3 :as d30] [:d4 d41 :as d4]]]
              (is (= [a b c0 d1 d2 d30 d41 d4]
                     [4 2 3 4 2 17 18 {:d41 18 :d42 :foo}]))
              (swap! call-count inc))]
      (f {:a 4 :c 3 :d {:d1 4 :d3 17 :d4 {:d41 18 :d42 :foo}}})
      ((fnk-impl/positional-fn f [:d :a :c])
       {:d1 4 :d3 17 :d4 {:d41 18 :d42 :foo}} 4 3))
    (is (= @call-count 4))
    (is (thrown? Throwable ((fnk [a] a) {:b 3})))

    (doseq [f [(fnk [] {:a 1 :b {:b1 2}})
               (fnk f :- {:a s/Any :b {:b1 s/Any}} []
                 (hash-map :a 1 :b {:b1 2} :c 3))]]
      (is (= (pfnk/output-schema f) {:a s/Any :b {:b1 s/Any}})))
    (let [a :k]
      (is (= (pfnk/output-schema (fnk [a] {a a})) s/Any)))))

(deftest fnk-input-schema-test
  (testing "simple fnk with one string key"
    (doseq [[t f] {"no-as" (fnk [a :- String] a)
                   "with-as" (fnk [a :- String :as b] a)}]
      (testing t
        (is (= {:a String s/Keyword s/Any}
               (pfnk/input-schema f)))
        (is (= "hi" (f {:a "hi"})))
        (is (= "hi" (f {:a "hi" :b 123})))
        (is (thrown? Exception (f {:a :lo})))
        (is (thrown? Exception (f {:a "hi" "b" "no-string-keys"})))))
    (is (= :lo ((fnk ^:never-validate foo [a :- String] a) {:a :lo}))))

  (testing "schemas on nested and optional bindings"
    (doseq [[t f] {"no-as" (fnk [a :- String {b :- String "1"} [:c d :- s/Number]]
                             [a b d])
                   "with-as" (fnk [a :- String {b :- String "1"} [:c d :- s/Number] :as m]
                               [a b d])}]
      (testing t
        (is (= {:a String
                (s/optional-key :b) String
                :c {:d s/Number s/Keyword s/Any}
                s/Keyword s/Any}
               (pfnk/input-schema f)))
        (is (= ["hi" "1" 2] (f {:a "hi" :c {:d 2}})))
        (is (= ["hi" "1" 2] (f {:a "hi" :c {:d 2 :e 3} :f :g})))
        (is (= ["hi" "bye" 2] (f {:a "hi" :b "bye" :c {:d 2}})))
        (is (thrown? Exception (f {:a "hi" :c {:d "2"}})))
        (is (thrown? Exception (f {:a "hi" :b :bye :c {:d 2}}))))))

  (testing "schemas on & bindings"
    (let [f (fnk [a :- String [:b c & more :- {s/Keyword s/Number}] & more :- {}]
              [a c])]
      (is (= {:a String
              :b {:c s/Any s/Keyword s/Number}}
             (pfnk/input-schema f)))
      (is (= ["hi" 1] (f {:a "hi" :b {:c 1}})))
      (is (= ["hi" 1] (f {:a "hi" :b {:c 1 :z 3}})))
      (is (thrown? Exception (f {:a "hi" :b {:c 1 :z "3"}})))
      (is (thrown? Exception (f {:a "hi" :b {:c 1} :d :e})))))

  (testing "schema override on top-level map bindings"
    (let [override {:a s/Number (s/optional-key :b) String (s/optional-key :e) String}]
      (doseq [[t f] {"no-as" (fnk [a :- String {b :- String "1"}] :- override
                               [a b])
                     "with-as" (fnk [a :- String {b :- String "1"} :as m] :- override
                                 [a b])}]
        (testing t
          (is (= override (pfnk/input-schema f)))
          (is (= [2 "1"] (f {:a 2})))
          (is (= [2 "2"] (f {:a 2 :b "2"})))
          (is (= [2 "2"] (f {:a 2 :b "2" :e "asdf"})))
          (is (thrown? Exception (f {:a "2"})))
          (is (thrown? Exception (f {:a 2 :b 2})))
          (is (thrown? Exception (f {:a 2 :z :huh})))))))

  (testing "schema override on inner map bindings"
    (let [f (fnk [a :- String [:b c] :- {:c String}]
              [a c])]
      (is (= {:a String :b {:c String} s/Keyword s/Any} (pfnk/input-schema f)))
      (is (= ["1" "2"] (f {:a "1" :b {:c "2"}})))
      (is (thrown? Exception (f {:a "1" :b {:c 2}})))
      (is (thrown? Exception (f {:a "1" :b {:c "2" :d "3"}}))))))

(defnk keyfn-test-docstring "whoa" [dude {wheres :foo} :as my & car]
  [dude wheres my car])

(defnk keyfn-test-no-docstring  [{city :sf} foo]
  [foo city])

(deftest defnk-test
  (is (= [11 :foo {:dude 11 :sweet 17} {:sweet 17}]
         (keyfn-test-docstring {:dude 11 :sweet 17})))
  (is (= [:foo :sf] (keyfn-test-no-docstring {:foo :foo})))
  (is (= [{:foo s/Any (s/optional-key :city) s/Any s/Keyword s/Any} s/Any]
         (pfnk/io-schemata keyfn-test-no-docstring)))
  (is (thrown? Throwable (keyfn-test-docstring :wheres :mycar))))

;; Test that type hints are properly propagated for fnk and defnk.
(defnk ^Byte a-typehinted-defnk [^Long l]
  (.byteValue l))

(deftest type-hints-test
  (is (= Byte (:tag (meta #'a-typehinted-defnk))))
  (doseq [f [a-typehinted-defnk
             (fnk [^Long l] (.byteValue l))
             (fnk [{^Long l 1}] (.byteValue l))
             (fnk [^Long l & m] (.byteValue l))]]
    (is (= (Byte. (byte 1)) (f {:l (Long. 1)})))
    (is (thrown? Exception (f {:l (Integer. 1)})))))

(deftest ^:slow repeated-bindings-test
  (is (thrown? Exception (eval '(fnk [x [:x y]] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [{x {:y 1}} [:x y]] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [x :as x] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [x & x] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [{x {:y 1}} x] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [x [:x y] :as m] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [{x {:y 1}} [:x y] :as m] (+ x y)))))
  (is (thrown? Exception (eval '(fnk [{x {:y 1}} x :as m] (+ x y))))))

(deftest optional-self-shadow-test
  (is (= 1 (let [b 1] ((fnk [{a b}] a) {}))))
  (doseq [[desc f] (let [a 1]
                     {"pos" (fnk [{a a}] a)
                      "non-pos" (fnk [{a a} :as m] a)})]
    (testing desc
      (is (= 1 (f {})))
      (is (= 2 (f {:a 2}))))))

(deftest optional-cross-arg-shadow-test
  (doseq [[desc f] (let [a 1 b 2 c 3 e 4 e 5]
                     {"pos" (fnk [c {a c} {b a} {d e} e] [a b c d e])
                      "non-pos" (fnk [c {a c} {b a} {d e} e :as m] [a b c d e])})]
    (testing desc
      (is (= [6 7 8 9 10] (f {:a 6 :b 7 :c 8 :d 9 :e 10})))
      (is (= [8 7 8 9 10] (f {:b 7 :c 8 :d 9 :e 10})))
      (is (= [8 8 8 9 10] (f {:c 8 :d 9 :e 10})))
      (is (= [8 8 8 5 10] (f {:c 8 :e 10}))))))

(deftest dont-shadow-nested-test
  (let [m {:x 1}]
    (is (= 3 ((fnk [[:m x]] (+ x (:x m))) {:m {:x 2}})))))

(use-fixtures :once schema-test/validate-schemas)
