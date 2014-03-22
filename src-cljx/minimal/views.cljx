(ns minimal.views
  #+cljs  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as str]
            #+clj [clojure.java.shell :refer [sh]]
            #+clj [cheshire.core :refer [generate-string]]
            #+cljs [om.core :as om :include-macros true]
            #+cljs [om.dom :as dom :include-macros true]
            #+cljs [cljs.core.async :refer [put! chan <!]]
            [minimal.data :refer [app-state]]))

#+cljs
(defn contact-view [contact owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/li nil
              (dom/span nil (:first contact))
              (dom/button
               #js {:onClick
                    (fn [e]
                      (do
                        (js/alert "delete!")
                        (put! delete @contact)))}
               "Delete")))))


#+cljs
(defn contacts-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan)})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
          (let [contact (<! delete)]
            (om/transact! app :contacts
              (fn [xs] (vec (remove #(= contact %) xs))))
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/div nil
        (dom/h2 nil "Contact list")
        (apply dom/ul nil
          (om/build-all contact-view (:contacts app)
            {:init-state {:delete delete}}))))))

#+clj
(defn js-calling-code [fn-name args]
  (str
   "console.log("
   fn-name "("
   "cljs.core.js__GT_clj(" (generate-string args) ","
   "new cljs.core.Keyword(null,  \"keywordize-keys\", \"keywordize-keys\"),"
   "true"
   ")));"))

#+clj
(defn node-call-fn [fn-name args]
 (sh "node"
     :in (str (str/replace (slurp "main.js") "#!/usr/bin/env node" "") ";\n"
              (js-calling-code fn-name args))))

(defn template-string [state]
  #+cljs
  (.renderComponentToString
   js/React
   (om/build contacts-view state))
  #+clj
  (let [{:keys [out err exit]}
        (node-call-fn "minimal.views.template_string" state)]
    (if (= 0 exit) out (throw (Exception. err)))))
