(ns popup.core
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [<!] :as async]
            [util.storage :as storage]))

(defn set-keybinding [row-num letter ctrl]
  (let [table-row (aget (.querySelectorAll js/document "tr") (inc row-num))
        row-elements (.querySelectorAll table-row "input")
        letter-elem (aget row-elements 0)
        ctrl-elem (aget row-elements 1)]
    (aset letter-elem "value" letter)
    (aset ctrl-elem "checked" ctrl)))

(defn get-and-set-data-from-storage []
  (storage/put-data-in-callback
   (fn [data]
     (let [ctrl (data "expand-parent-is-ctrl")
           letter (data "expand-parent-letter")]
       (set-keybinding 0 letter ctrl)))))

(defn get-keybinding [row-num]
  (let [table-row (aget (.querySelectorAll js/document "tr") (inc row-num))
        row-elements (.querySelectorAll table-row "input")
        letter (.-value (aget row-elements 0))
        checked (.-checked (aget row-elements 1))]
    (vector letter checked)))

(defn on-btn-click []
  (let [[letter ctrl] (get-keybinding 0)]
    (storage/update-expand-parent! letter ctrl)))

(defn dom-ready []
  (.addEventListener (.getElementById js/document "submit-btn")
                     "click" on-btn-click)
  (get-and-set-data-from-storage))

(defn init! []
  (.addEventListener js/window "load" dom-ready))

;; (.removeEventListener (.getElementById js/document "submit-btn") "click" on-btn-click)

