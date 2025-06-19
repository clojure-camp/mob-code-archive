(defproject exercises "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [http-kit "2.8.0-RC1"]
                 [cheshire "5.13.0"]
                 [metosin/malli "0.17.0"]
                 [com.hyperfiddle/rcf "20220926-202227"]
                 [org.clojure/test.check "1.1.1"]
                 [hiccup "2.0.0-RC5"]
                 [criterium "0.4.6"]
                 [cljfx "1.9.5"]
                 [com.rpl/specter "1.1.4"]
                 
                 [com.github.seancorfield/next.jdbc "1.3.1048"]
                 [org.xerial/sqlite-jdbc "3.47.1.0"]
                 [com.h2database/h2 "2.3.232"]
                 [com.github.seancorfield/honeysql "2.7.1310"]
                 [com.layerware/hugsql "0.5.3"]]
  :repl-options {:init-ns exercises.core})
