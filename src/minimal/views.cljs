(ns minimal.views
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]

            [secretary.core :as secretary])
  (:import goog.history.Html5History
           goog.history.EventType
           goog.Uri))

;; formerly core
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
  ;; setup navigation. See http://closure-library.googlecode.com/git-history/6b23d1e05147f2e80b0d619c5ff78319ab59fd10/closure/goog/demos/html5history.html
  (goog.events/listen ;; when token changes, update view
   hist EventType/NAVIGATE
   #(let [{new-template :template new-state :state}
          (secretary/dispatch! (str "/" (.-token %)))]
      (.log js/console (str "token set to " (.-token %)))
      (render-root
       new-template new-state
       (.getElementById js/document "app0")))))

(defn setup-app [component state]
  (do
    (render-root component state (.getElementById js/document "app0"))))

(defn client-load! [e] ;; e should be the click event of an <a>
  (do
    (.setToken hist
               (-> e .-target (.getAttribute "href") strip-leading-slash)
               (-> e .-target .-title))
    (.preventDefault e)))

;; end formerly core

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
              (dom/a #js {:className "client-loadable"
                          :href (str "/contact/" (:first contact) "/")
                          :onClick client-load!}
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
    om/IRender
    (render [this]
      (dom/p nil
              (dom/h1 nil (str (:first contact) " " (:last contact)))
              (dom/h3 nil (:email contact))
              (dom/a #js {:href "/" :onClick client-load!} "all contacts")))))

(defn single-contact-view [contact owner]
  (reify
    om/IRender
    (render [this]
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
