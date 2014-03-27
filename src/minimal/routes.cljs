(ns minimal.routes
  (:require [secretary.core :as secretary
             :include-macros true :refer [defroute]]
            [minimal.data :refer [app-state get-contact]]
            [minimal.views :refer [single-contact-view contacts-view]]))

(enable-console-print!)

(defroute "/" []
  (do
    (.log js/console "index route")
    {:state app-state :template contacts-view}))

(defroute "/contact/:name/" [name]
  (do
    (.log js/console "single contact route")
    {:template single-contact-view
     :state (get-contact {:first name})}))
