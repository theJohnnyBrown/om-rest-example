(ns minimal.nodeserver
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [minimal.views :as views]
            [minimal.data :refer [app-state get-contact update-contact
                                  add-contact delete-contact]]
            [minimal.routes :as routes]
            [secretary.core :as secretary]))

; Node.js dirname
(def __dirname (js* "__dirname"))

(def express (nodejs/require "express"))
(def logfmt (nodejs/require "logfmt"))

(def app (express))

(def port (or (aget nodejs/process "env" "PORT") 3000))

; Logger
(.use app (.requestLogger logfmt))

(defn serialize [data]
  (->> data clj->js (.stringify js/JSON)))

(defn send-json! [res data]
  (doto res
    (.setHeader "Content-Type" "application/json")
    (.end (serialize data))))

(defn not-found! [res] (-> res (.status 404) (.end "Not found")))

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
      (fn [req res next]
        (let [body (js->clj (.-body req) :keywordize-keys true)
              result (update-contact body)]
          (send-json! res result))))

(.post app "/api/contacts/"
       (fn [req res next]
         (let [body (js->clj (.-body req) :keywordize-keys true)
               result (add-contact body)]
           (send-json! res result))))

(.delete app "/api/contacts/:id/"
         (fn [req res next]
           (let [body (js->clj (.-body req) :keywordize-keys true)
                 ct (get-contact (select-keys body [:first]))]
             (if ct
              (do
                (delete-contact body) (send-json! res ""))
              (not-found! res)))))

(defn render-html [template state]
  (str "<html><head>"
       "<title>minimal react cljx</title></head>"
       "<body id=\"app0\">"
       (views/template-string state template)
       "<script src=\"/js/app-dev.js\"></script> "
       "</body></html>"))

(.get app "*" (fn [req res next]
                (let [{:keys [template state] :as view}
                      (secretary/dispatch! (.-url req))]
                  (if view
                    (.send res (render-html template state))
                    (next)))))

(.get app "*" #(not-found! %2))

(defn -main [& args] (.listen app port))

(set! *main-cli-fn* -main)
