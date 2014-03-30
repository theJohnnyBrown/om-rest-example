(ns minimal.data
  (:require [om-sync.util :refer [json-xhr]]
                   [minimal.util :refer [browser?]]))

(def app-state
  (atom
    {:contacts
     [{:first "Ben", :last "Bitdiddle", :email "benb@mit.edu"}
      {:first "Alyssa", :middle-initial "P", :last "Hacker", :email "aphacker@mit.edu"}
      {:first "Eva", :middle "Lu", :last "Ator", :email "eval@mit.edu"}
      {:first "Louis", :last "Reasoner", :email "prolog@mit.edu"}
      {:first "Cy", :middle-initial "D", :last "Effect", :email "bugs@mit.edu"}
      {:first "Lem", :middle-initial "E", :last "Tweakit", :email "morebugs@mit.edu"}
      ]}))

(defn get-contact [params]
  (->> @app-state
       :contacts
       (filter #(= (merge % params) %))
       first))

(defn update-contact [new-contact]
  (let [ct (get-contact (select-keys new-contact [:first]))]
   (assert ct)
   (reset! app-state
     (update-in @app-state
       [:contacts] (fn [cts]
                     (vec (cons new-contact (remove #(= % ct) cts))))))
   (get-contact new-contact)))

(defn add-contact [ct]
  (assert (:first ct))
  (reset! app-state (update-in @app-state
                     [:contacts]
                     #(conj % (merge {:email "missing@example.com"} ct))))
  (get-contact ct))

(defn delete-contact [ct]
  (reset! app-state (update-in @app-state
                     [:contacts]
                     (fn [cts]
                       (vec (remove #(= (:first %) (:first ct)) cts))))))

;; this is basically an artifact of defining our data in source code
;; on the server the data in memory is preserved between requests, but the
;; client loads the source with the predefined data each time.
(if browser?
  (json-xhr
   {:url "/api/contacts/" :method :get
    :on-complete #(reset! app-state {:contacts %})
    :on-error #(js/alert (str %))}))
