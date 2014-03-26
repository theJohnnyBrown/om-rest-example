(ns minimal.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [minimal.views :refer [contacts-view contact-view]]
            [minimal.data :refer [app-state]]

            [minimal.routes :as routes]
            ;; because otherwise secretary.core/*routes* will be empty

            [secretary.core :as secretary])
   (:import goog.history.Html5History
            goog.history.EventType
            goog.Uri))

(enable-console-print!)

(extend-type js/NodeList
  ISeqable
  (-seq [array] (array-seq array 0)))

(defn render-root [component state target]
  (om/root component state {:target target}))

(defn setup-app [component state]
  (do
    (render-root component state (.getElementById js/document "app0"))
    ;; setup navigation. See http://closure-library.googlecode.com/git-history/6b23d1e05147f2e80b0d619c5ff78319ab59fd10/closure/goog/demos/html5history.html
    (let [h (Html5History.)]
      (goog.events/listen ;; when token changes, update view
       h EventType/NAVIGATE
       #(let [{new-template :template new-state :state}
              (secretary/dispatch! (str "/" (.-token %)))]
          (.log js/console (str "token set to " (.-token %)))
          (render-root
           new-template new-state
           (.getElementById js/document "app0"))))
      (doto h ;; configure history object
        (.setUseFragment false)
        (.setEnabled true))
      (doseq [elem (.getElementsByClassName js/document "client-loadable")]
        (goog.events/listen
         elem "click"
         (fn [e]
           (.setToken h
                      (-> e .-target (.getAttribute "href"))
                      (-> e .-target .-title))))))))

;; only run in browser
(if (exists? js/document)
  (do
    (.log js/console "starting app")
    (.log js/console (-> js/window .-location .-pathname))
    (.log js/console (str (:state
                           (secretary/dispatch!
                            (-> js/window .-location .-pathname)))))
    (let [{:keys [template state]} (secretary/dispatch!
                                    (-> js/window .-location .-pathname))]
      (setup-app template state))))
