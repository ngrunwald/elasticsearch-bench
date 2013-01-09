(ns bench-es.core
  (:require [clj-elasticsearch.client :as c]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.rest          :as esr]))

(defn prepare-docs
  [txt]
  (let [words (str/split txt #"\s+")
        [docs queries] (loop [todo words
                              docs []
                              queries []]
                         (if-not (empty? todo)
                           (let [[doc-words left] (split-at 140 todo)
                                 doc (str/join " " doc-words)]
                             (recur left (conj docs doc) (conj queries (nth doc-words 25))))
                           [docs queries]))]
    [docs queries]))

(defn launch-local-es
  []
  (c/make-node {:local-mode true :client-mode false}))

(defn delete-dir
  [path]
  (let [d (io/file path)]
    (if (.isDirectory d)
      (do
        (doseq [f (seq (.listFiles d))]
          (delete-dir f)
          (.delete f))
        (.delete d))
      (.delete d))))

(defn native-index-docs
  [docs]
  (doall
   (for [doc docs]
     (:id (c/index-doc {:source {:text doc} :index "test" :type "lit"})))))

(defn native-get-docs
  [ids]
  (doseq [id ids]
    (c/get-doc {:index "test" :type "lit" :id id})))

(defn native-search
  [queries]
  (doseq [q queries]
    (c/search {:indices ["test"] :types ["lit"] :extra-source {:query {:term {:text q}}}})))

(defn rest-index-docs
  [docs]
  (doall
   (for [doc docs]
     (:_id (esd/create "test" "lit" {:text doc})))))

(defn rest-get-docs
  [ids]
  (doseq [id ids]
    (esd/get "test" "lit" id)))

(defn rest-search
  [queries]
  (doseq [q queries]
    (esd/search "test" "lit" :query (q/term :text q))))

;; On my machine and resources/pg2600.txt about ~9s
(defn bench-native
  [docs queries]
  (c/with-node-client {:local-mode true}
    (time
     (do
       (println "timing" (count docs) "indexations")
       (let [ids (time (native-index-docs docs))]
         (println "done!")
         (println "getting documents")
         (time (native-get-docs ids))
         (println "done!")
         (println "timing" (count queries) "searches")
         (time (native-search queries))
         (println "done!")
         (println "total-time"))))))

;; On my machine and resources/pg2600.txt about ~51s
(defn bench-rest
  [docs queries]
  (esr/connect! "http://127.0.0.1:9200")
  (time
   (do
     (println "=> timing" (count docs) "indexations")
     (let [ids (time (rest-index-docs docs))]
       (println "done!")
       (println "=> timing getting documents")
       (time (rest-get-docs ids))
       (println "done!")
       (println "=> timing" (count queries) "searches")
       (time (rest-search queries))
       (println "done!")
       (println "=> total-time")))))

(defn -main
  [path type]
  (let [node (launch-local-es)
        txt (slurp path)
        [docs queries] (prepare-docs txt)]
    (if (= type "rest")
      (do
        (println "=> Benching with REST client (Elastisch)")
        (bench-rest docs queries))
      (do
        (println "=> Benching with native client (clj-elasticsearch)")
        (bench-native docs queries)))
    (.close node)
    (delete-dir "data")))
