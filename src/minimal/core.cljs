(ns minimal.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [minimal.views :refer [contacts-view contact-view]]
            [minimal.data :refer [app-state]]))

(enable-console-print!)


;; in practice we probably want separate cljsbuild builds for node and browser
;; browser-main and node-main should be in separate namespaces which are only
;; compiled for the appropriate environment

(defn browser-main []
  (om/root
   (fn [app owner]
     (dom/h1 nil (:text app)))
   app-state
   {:target (. js/document (getElementById "app"))}))

(defn template-string []
 (.renderComponentToString
   js/React
   (om/build contacts-view @app-state)))

;; (defn node-main []
;;  (.log
;;   js/console (template-string)))

(set! *main-cli-fn* (fn [] nil))
