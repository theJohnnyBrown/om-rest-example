(defproject minimal "0.1.0-SNAPSHOT"
  :description "An example of DRY client+server templating and routing,
                using om and secretary"
  :url "https://github.com/theJohnnyBrown/om-rest-example"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.3.1"]
                 [ring "1.2.0"]
                 [compojure "1.1.5"]
                 [enlive "1.1.4"]
                 [org.clojure/tools.logging "0.2.6"]
                 [liberator "0.9.0"]
                 [ch.qos.logback/logback-classic "1.0.13"]

                 [org.clojure/clojurescript "0.0-2197"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [om "0.5.0"]
                 [secretary "1.1.0"]]

  :plugins [[lein-npm "0.3.2"]
            [lein-cljsbuild "1.0.2"]
            [lein-ring "0.8.8"]]

  :ring {
    :handler minimal.server/app
    :nrepl {:start? true :port 4555}}

  :resource-paths ["public" "html"]

  :node-dependencies [[react "0.9.0"]
                      [domino "1.0.15"]
                      [express "3.4.8"]
                      [logfmt "0.20.0"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "node"
              :source-paths ["src" "src-node"]
              :compiler {
                :language-in :ecmascript5
                :target :nodejs
                :output-to "main.js"
                :optimizations :simple
                :pretty-print true
                :preamble ["minimal/react_preamble.js"]
                :externs ["react/externs/react.js"]}}
             {:id "browser-dev"
              :source-paths ["src"]
              :compiler {
                :output-to "public/js/app-dev.js"
                :optimizations :simple
                :pretty-print true
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}
             {:id "browser"
              :source-paths ["src"]
              :compiler {
                :output-to "public/js/app.js"
                :optimizations :advanced
                :pretty-print true
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}]})
