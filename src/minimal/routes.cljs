(ns minimal.routes
  (:require [secretary.core :as secretary
             :include-macros true :refer [defroute]]
            [minimal.data :refer [app-state get-contact]]
            [minimal.views :refer [single-contact-view contacts-view]]))

(defroute "/" [] {:state app-state :template contacts-view})

(defroute "/contact/:name/" [name]
  (let [ct (get-contact {:first name})]
    (if ct {:template single-contact-view :state ct})))
