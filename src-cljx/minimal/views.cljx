(ns minimal.views
  #+cljs  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as str]
            #+clj [clojure.java.shell :refer [sh]]
            #+clj [cheshire.core :refer [generate-string]]
            #+cljs [om.core :as om :include-macros true]
            #+cljs [om.dom :as dom :include-macros true]
            #+cljs [cljs.core.async :refer [put! chan <!]]))

(defn parse-contact [contact-str]
  (let [[first middle last :as parts] (str/split contact-str #"\s+")
        [first last middle] (if (nil? last) [first middle] [first last middle])
        middle (when middle (str/replace middle "." ""))
        c (if middle (count middle) 0)]
    (when (>= (count parts) 2)
      (cond-> {:first first :last last}
        (== c 1) (assoc :middle-initial middle)
        (>= c 2) (assoc :middle middle)))))

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
(defn add-contact [e owner app]
  (let [new-contact (-> (om/get-node owner "new-contact")
                        .-value
                        parse-contact)]
    (when new-contact
      (do
       (om/transact! app :contacts #(conj % new-contact))
       (om/set-state! owner :text "")))))

#+cljs
(defn handle-change [e owner {:keys [text]}]
  (om/set-state! owner :text (.. e -target -value)))

#+cljs
(defn contacts-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan) :text ""})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
          (let [contact (<! delete)]
            (om/transact! app :contacts
              (fn [xs] (vec (remove #(= contact %) xs))))
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [delete] :as state}]
      (dom/div nil
        (dom/h2 nil "Contact list")
        (apply dom/ul nil
          (om/build-all contact-view (:contacts app)
            {:init-state {:delete delete}}))
        (dom/div nil
          (dom/input #js {:type "text" :ref "new-contact" :value (:text state)
                          :onChange #(handle-change % owner state)})
          (dom/button #js {:onClick #(add-contact % owner app)} "Add contact"))))))


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
