(ns minimal.views
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))

(defn parse-contact [contact-str]
  (let [[first middle last :as parts] (str/split contact-str #"\s+")
        [first last middle] (if (nil? last) [first middle] [first last middle])
        middle (when middle (str/replace middle "." ""))
        c (if middle (count middle) 0)]
    (when (>= (count parts) 2)
      (cond-> {:first first :last last}
        (== c 1) (assoc :middle-initial middle)
        (>= c 2) (assoc :middle middle)))))

(defn contact-view [contact owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/li nil
              (dom/a #js {:class "client-loadable"
                          :href (str "/contact/" (:first contact) "/")}
                     (:first contact))
              (dom/button
               #js {:onClick
                    (fn [e]
                      (do
                        (js/alert "delete!")
                        (put! delete @contact)))}
               "Delete")))))

(defn single-contact-view* [contact owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/p nil
              (dom/h1 nil (str (:first contact) (:last contact)))
              (dom/h3 nil (:email contact))
              (dom/button
               #js {:onClick
                    (fn [e]
                      (do
                        (js/alert "delete!")
                        (put! delete @contact)))}
               "Delete")))))

(defn single-contact-view [contact owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan)})
    om/IRenderState
    (render-state [this {:keys [delete] :as state}]
      (dom/div nil
        (om/build single-contact-view* contact)))))

(defn add-contact [e owner app]
  (let [new-contact (-> (om/get-node owner "new-contact")
                        .-value
                        parse-contact)]
    (when new-contact
      (do
       (om/transact! app :contacts #(conj % new-contact))
       (om/set-state! owner :text "")))))


(defn handle-change [e owner {:keys [text]}]
  (om/set-state! owner :text (.. e -target -value)))


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

(defn template-string [state component]
  (.renderComponentToString
   js/React
   (om/build component state)))
