(ns minimal.core
  (:require [minimal.routes :as routes]
            ;; because otherwise secretary.core/*routes* will be empty

            [minimal.views :as views]
            [minimal.util :refer [browser?]]
            [secretary.core :as secretary]))


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
      (views/setup-app template state))))
