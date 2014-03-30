(ns minimal.routes
  (:require [secretary.core :as secretary
             :include-macros true :refer [defroute]]
            [minimal.data :refer [app-state get-contact]]
            [minimal.views :refer [single-contact-view contacts-view]]))

(defroute "/" [] {:state app-state :template contacts-view})

(defroute "/contact/:name/" [name] {:template single-contact-view
                                    :state (get-contact {:first name})})
