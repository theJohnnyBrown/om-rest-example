(ns minimal.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [minimal.views :refer [contacts-view contact-view]]
            [minimal.data :refer [app-state]]

            [minimal.routes :as routes]
            ;; because otherwise secretary.core/*routes* will be empty

            [secretary.core :as secretary])
   (:import goog.history.Html5History
            goog.history.EventType
            goog.Uri))

(def browser? (exists? js/document))

(enable-console-print!)

(if browser?
 (extend-type js/NodeList
   ISeqable
   (-seq [array] (array-seq array 0))))

(def hist (if browser?
           (doto (Html5History.) (.setUseFragment false) (.setEnabled true))))

(defn strip-leading-slash [s] (if (= (str (first s)) "/")
                               (str/join (rest s)) s))

(defn render-root [component state target]
  (om/root component state {:target target})
  (doseq [elem (.getElementsByClassName js/document "client-loadable")]
    (goog.events/listen
     elem "click"
     (fn [e]
       (do
         (.setToken hist
                    (-> e .-target (.getAttribute "href") strip-leading-slash)
                    (-> e .-target .-title))
         (.preventDefault e))))))

(defn setup-app [component state]
  (do
    (render-root component state (.getElementById js/document "app0"))
    ;; setup navigation. See http://closure-library.googlecode.com/git-history/6b23d1e05147f2e80b0d619c5ff78319ab59fd10/closure/goog/demos/html5history.html
    (goog.events/listen ;; when token changes, update view
     hist EventType/NAVIGATE
     #(let [{new-template :template new-state :state}
            (secretary/dispatch! (str "/" (.-token %)))]
        (.log js/console (str "token set to " (.-token %)))
        (render-root
         new-template new-state
         (.getElementById js/document "app0"))))))

;; only run in browser
(if browser?
  (do
    (.log js/console "starting app")
    (.log js/console (-> js/window .-location .-pathname))
    (.log js/console (str (:state
                           (secretary/dispatch!
                            (-> js/window .-location .-pathname)))))
    (let [{:keys [template state]} (secretary/dispatch!
                                    (-> js/window .-location .-pathname))]
      (setup-app template state))))
