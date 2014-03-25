(ns minimal.nodeserver
  (:require [cljs.nodejs :as nodejs]
            [minimal.views :as views]
            [minimal.data :refer [app-state]]))

; Node.js dirname
(def __dirname (js* "__dirname"))

(def express (nodejs/require "express"))

(def app (express))

(def port (or (aget nodejs/process "env" "PORT") 3000))

; Body parser
(.use app (.urlencoded express))
(.use app (.json express))

; Set assets folder
(.use app (.static express (str __dirname "/public")))



(.get app "/" #(.send %2
                      (str "<html><head>"
                           "<title>minimal react cljx</title></head>"
                           "<body id=\"app0\">"
                           (views/template-string @app-state)
                           "<script src=\"/js/app-dev.js\"></script> "
                           "</body></html>")))

(.get app "*" #(.send %2 (views/layout-render views/four-oh-four "404") 404))

(defn -main [& args]
  (.listen app port))

(set! *main-cli-fn* -main)
