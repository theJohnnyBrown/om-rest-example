(defproject minimal "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.5.0"]]

  :plugins [[lein-npm "0.3.2"]
            [lein-cljsbuild "1.0.2"]]

  :node-dependencies [domino "1.0.15"]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "release"
              :source-paths ["src"]
              :compiler {
                :output-to "main.js"
                :optimizations :advanced
                :pretty-print false
                :preamble ["minimal/fake_document.js"]
                :externs ["react/externs/react.js"]}}]})
