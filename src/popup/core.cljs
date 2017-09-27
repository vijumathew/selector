(ns popup.core
  (:require-macros [cljs.core.async.macros :refer [go-loop go]])
  (:require [cljs.core.async :refer [<!] :as async]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.protocols :refer [post-message!]]
            [chromex.ext.runtime :as runtime :refer-macros [connect]]
            [util.storage :as storage]))

; -- a message loop ---------------------------------------------------------------------------------------------------------
(comment
  (defn process-message! [message]
    (log "POPUP: got message:" message))

  (defn run-message-loop! [message-channel]
    (log "POPUP: starting message loop...")
    (go-loop []
      (when-some [message (<! message-channel)]
        (process-message! message)
        (recur))
      (log "POPUP: leaving message loop")))

  (defn connect-to-background-page! []
    (let [background-port (runtime/connect)]
      (post-message! background-port "hello from POPUP!")
      (run-message-loop! background-port))))

;; my popup
(defn set-keybinding [row-num letter ctrl]
  (let [table-row (aget (.querySelectorAll js/document "tr") (inc row-num))
        row-elements (.querySelectorAll table-row "input")
        letter-elem (aget row-elements 0)
        ctrl-elem (aget row-elements 1)]
    (aset letter-elem "value" letter)
    (aset ctrl-elem "checked" ctrl)))

;; send message directly to content script
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
    (storage/update-expand-parent! letter ctrl)
    (.log js/console (str letter " " ctrl))
    (print (str letter " " ctrl)))
  (.log js/console "sup"))

;; implement listener for updating the keybinding in popup
;; and then copy this to content script
;; and that should be it....

(defn init! []
  (log "POPUP: init")
  ;;(connect-to-background-page!)
  (.addEventListener (.getElementById js/document "submit-btn") "click" on-btn-click)
  (get-and-set-data-from-storage))

;;
;; (.removeEventListener (.getElementById js/document "submit-btn") "click" on-btn-click)

