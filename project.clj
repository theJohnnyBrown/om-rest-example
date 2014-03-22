(defproject minimal "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.3.1"]
                 [ring "1.2.0"]
                 [compojure "1.1.5"]
                 [enlive "1.1.4"]
                 [org.clojure/tools.logging "0.2.6"]
                 [liberator "0.9.0"]
                 [ch.qos.logback/logback-classic "1.0.13"]

                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.5.0"]]

  :plugins [[lein-npm "0.3.2"]
            [lein-cljsbuild "1.0.2"]
            [com.keminglabs/cljx "0.3.2"]
            [lein-ring "0.8.8"]]

  :ring {
    :handler minimal.server/app
  }

  :resource-paths ["public" "html"]

  :cljx {:builds [{:source-paths ["src-cljx"]
                   :output-path "src"
                   :rules :clj}
                  {:source-paths ["src-cljx"]
                   :output-path "src"
                   :rules :cljs}]}

  :hooks [cljx.hooks]

  :node-dependencies [[react "0.9.0"]
                      [domino "1.0.15"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "node"
              :source-paths ["src"]
              :compiler {
                :target :nodejs
                :output-to "main.js"
                :optimizations :simple
                :pretty-print true
                :preamble ["minimal/fake_document.js"]
                :externs ["react/externs/react.js"]}}]})
