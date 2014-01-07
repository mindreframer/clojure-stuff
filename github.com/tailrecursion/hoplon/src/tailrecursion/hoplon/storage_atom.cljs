;; Copyright (c) Alan Dipert and Micha Niskin. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns tailrecursion.hoplon.storage-atom
  (:require [cljs.reader :refer [read-string]]))

(defprotocol IStorageBackend
  "Represents a storage resource."
  (-get [this not-found])
  (-commit! [this value] "Commit value to storage at location."))

(deftype StorageBackend [store key]
  IStorageBackend
  (-get [this not-found]
    (if-let [existing (.getItem store (pr-str key))]
      (read-string existing)
      not-found))
  (-commit! [this value]
    (.setItem store (pr-str key) (pr-str value))))

(defn store
  [atom backend]
  (let [existing (-get backend ::none)]
    (if (= ::none existing)
      (-commit! backend @atom)
      (reset! atom existing))
    (doto atom
      (add-watch ::storage-watch #(-commit! backend %4)))))

(defn html-storage
  [atom storage k]
  (store atom (StorageBackend. storage k)))

(defn local-storage
  [atom k]
  (html-storage atom js/localStorage k))

(defn session-storage
  [atom k]
  (html-storage atom js/sessionStorage k))
