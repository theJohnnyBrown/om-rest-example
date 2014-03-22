(ns minimal.views
  (:require #+clj [clojure.java.shell :refer [sh]]
            #+clj [cheshire.core :refer [generate-string]]
            #+cljs [om.core :as om :include-macros true]
            #+cljs [om.dom :as dom :include-macros true]))

#+cljs
(defn contact-view [contact owner]
  (reify
    om/IRender
    (render [this]
      (dom/li nil
              (dom/span nil (:first contact))
              (dom/button nil "Delete")))))


#+cljs
(defn contacts-view [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/body nil
       (dom/div nil (:message app))
       (dom/div #js {:id "app"}
         (dom/h2 nil "Contact list")
         (apply dom/ul nil
           (om/build-all contact-view (:contacts app))))))))

(defn template-string [state]
  #+cljs
  (.renderComponentToString
   js/React
   (om/build contacts-view state))
  #+clj
  (:out (sh "node"
          :in (str (slurp "main.js")
                   ";\n" "console.log(minimal.core.template_string("
                   "cljs.core.js__GT_clj(" (generate-string state) ","
                   "new cljs.core.Keyword(null,  \"keywordize-keys\", \"keywordize-keys\")"
                   ")));"))))
