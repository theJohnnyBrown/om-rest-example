(ns minimal.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [minimal.views :refer [contacts-view contact-view]]
            [minimal.data :refer [app-state]]))

(enable-console-print!)


;; in practice we probably want separate cljsbuild builds for node and browser,
;; in separate namespaces which are only
;; compiled for the appropriate environment


(defn setup-app []
 (om/root contacts-view app-state
  {:target (.getElementById js/document "app0")}))

;; only run in browser
(if (exists? js/document)
  (do
    (.log js/console "starting app")
    (setup-app)))


;; prevents a nullPointerException type situation when running on node
;; (set! *main-cli-fn* (fn [] nil))
