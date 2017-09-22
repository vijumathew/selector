(ns popup.core
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :refer [<!]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.protocols :refer [post-message!]]
            [chromex.ext.runtime :as runtime :refer-macros [connect]]))

; -- a message loop ---------------------------------------------------------------------------------------------------------

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
    (run-message-loop! background-port)))

; -- main entry point -------------------------------------------------------------------------------------------------------

(defn init! []
  (log "POPUP: init")
  (connect-to-background-page!))

(defn set-keybinding [row-num letter ctrl]
  (let [table-row (aget (.querySelectorAll js/document "tr") (inc row-num))
        row-elements (.querySelectorAll table-row "input")
        letter-elem (aget row-elements 0)
        ctrl-elem (aget row-elements 1)]
    (aset letter-elem "value" letter)
    (aset ctrl-elem "checked" ctrl)))

;; send message directly to content script
(defn get-keybinding [row-num]
  (let [table-row (aget (.querySelectorAll js/document "tr") (inc row-num))
        row-elements (.querySelectorAll table-row "input")
        letter (.-value (aget row-elements 0))
        checked (.-checked (aget row-elements 1))]
    (vector letter checked)))

(defn on-btn-click []
  (print (get-keybinding 0))
  (.log js/console "sup"))

(.addEventListener (.getElementById js/document "submit-btn") "click" on-btn-click)
;; (.removeEventListener (.getElementById js/document "submit-btn") "click" on-btn-click)

