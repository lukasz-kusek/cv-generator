(defproject cv-generator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-time "0.11.0"]
                 [org.clojure/clojure "1.7.0"]
                 [cheshire "5.5.0"]
                 [stencil "0.5.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [me.raynes/fs "1.4.6"]
                 ]
  :main ^:skip-aot cv-generator.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
