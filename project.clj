(defproject bench-es "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.elasticsearch/elasticsearch "0.20.2"]
                 [clojurewerkz/elastisch "1.0.2"]]
  :profiles {:new {:dependencies [[clj-elasticsearch "0.4.0-SNAPSHOT"]]}
             :old {:dependencies [[clj-elasticsearch "0.3.3"]]}}
  :main bench-es.core)
