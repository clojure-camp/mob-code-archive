(defproject exercises "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [http-kit "2.8.0-RC1"]
                 [cheshire "5.13.0"]
                 [metosin/malli "0.17.0"]
                 [com.hyperfiddle/rcf "20220926-202227"]
                 [criterium "0.4.6"]
                 [com.rpl/specter "1.1.4"]]
  :repl-options {:init-ns exercises.core})
