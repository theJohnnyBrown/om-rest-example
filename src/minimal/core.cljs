(ns minimal.core
  (:require [minimal.routes :as routes]
            ;; because otherwise secretary.core/*routes* will be empty

            [minimal.views :as views]
            [minimal.util :refer [browser?]]
            [secretary.core :as secretary]))


;; only run in browser
(if browser?
  (do
    (let [{:keys [template state]} (secretary/dispatch!
                                    (-> js/window .-location .-pathname))]
      (views/setup-app template state))))
