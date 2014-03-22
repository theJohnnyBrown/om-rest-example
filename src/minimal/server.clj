(ns minimal.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET ANY]]
            [compojure.route :refer [not-found]]
            [compojure.handler :as handler]
            [ring.util.mime-type :refer [ext-mime-type]]
            [liberator.core :refer [defresource]]
            [minimal.views :as views]
            [minimal.data :refer [app-state]]))

(defn- normalized-file [dir path]
  (let [f (io/file dir path)]
    (when (.startsWith (.getCanonicalPath f) (.getCanonicalPath (io/file dir)))
          (if (.isDirectory f)
              (io/file f "index.html")
              f))))

(defresource static-directory [static-dir]
  :available-media-types
  #(let [f (normalized-file static-dir (get-in % [:request :route-params :*]))]
    (if-let [mime-type (ext-mime-type (.getName f))]
      [mime-type]
      ["application/octet-stream"]))

  :exists?
  #(if-let [f (normalized-file static-dir (get-in % [:request :route-params :*]))]
    [(.exists f) {::file f}])

  :handle-ok (fn [{f ::file}] f)

  :last-modified (fn [{f ::file}] (when f (.lastModified f))))

(defroutes routes
  (GET "/" []
       (str "<html><head>"
            "<title>minimal react cljx</title></head>"
            "<body id=\"app0\">"
            (views/template-string @app-state)
            "<script src=\"/js/app-dev.js\"></script> "
            "</body></html>"))
  (GET "/*" [] (static-directory "public/"))
  (not-found "Resource not found"))

(def app
  (-> routes
      (handler/site)))

(views/template-string @app-state)
