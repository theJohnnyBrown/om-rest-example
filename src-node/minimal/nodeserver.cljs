(ns minimal.nodeserver
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [minimal.views :as views]
            [minimal.data :refer [app-state get-contact update-contact
                                  add-contact]]
            [minimal.routes :as routes]
            [secretary.core :as secretary]))

; Node.js dirname
(def __dirname (js* "__dirname"))

(def express (nodejs/require "express"))

(def app (express))

(def port (or (aget nodejs/process "env" "PORT") 3000))

(defn serialize [data]
  (->> data clj->js (.stringify js/JSON)))

(defn send-json! [res data]
  (doto res
    (.setHeader "Content-Type" "application/json")
    (.end (serialize data))))

; Body parser
(.use app (.urlencoded express))
(.use app (.json express))

; Set assets folder
(.use app (.static express (str __dirname "/public")))

(.get app "/api/contacts/:id/"
      (fn [req res next]
        (send-json! res (get-contact {:first (-> req .-params .-id)}))))

(.get app "/api/contacts/"
      (fn [req res next]
        (send-json! res (:contacts @app-state))))

(.put app "/api/contacts/:id/"
      ;; (.bodyParser express) ;; todo wrap this in js->clj and make reusable
      (fn [req res next]
        (let [body (js->clj (.-body req) :keywordize-keys true)
              result (update-contact body)]
          (send-json! res result))))

(.post app "/api/contacts/"
       (fn [req res next]
         (let [body (js->clj (.-body req) :keywordize-keys true)
               result (add-contact body)]
           (send-json! res result))))

(.get app "*" (fn [req res next]
                (.log js/console (.-url req))
                (.log js/console (.-token req))
                (.send res
                       (str "<html><head>"
                            "<title>minimal react cljx</title></head>"
                            "<body id=\"app0\">"
                            (let [{:keys [template state]}
                                  (secretary/dispatch! (.-url req))
                                  state-value @state]
                              (views/template-string state-value template))
                            "<script src=\"/js/app-dev.js\"></script> "
                            "</body></html>"))))

;; (.get app "*" #(.send %2 (views/layout-render views/four-oh-four "404") 404))

(defn -main [& args] (.listen app port))

(set! *main-cli-fn* -main)
