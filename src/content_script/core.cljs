(ns content-script.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! chan]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.protocols :refer [post-message!]]
            [chromex.ext.runtime :as runtime :refer-macros [connect send-message]]
            [util.selection :as util]
            [util.storage :as storage]))

;; structure is [key isCtrl?]
(def keybinding (atom [nil nil]))

(defn update-key! [letter ctrl]
  (swap! keybinding #(mapv (fn [old new]
                             (if (nil? new) old new)) %1 %2)
         [letter ctrl]))

(def key-chan (chan 20))

(defn key-handler [e]
  (let [letter (.-key e)
        ctrl (.-ctrlKey e)]
    (cljs.core.async/put! key-chan [letter ctrl])))

(defn process-key [e]
  (print e)
  (.log js/console @keybinding)
  (when (= e @keybinding)
    (util/expand-current-selection)
    (print "MATCH")))

(def key-loop
  (go-loop []
    (when-some [message (<! key-chan)]
      (process-key message)
      (recur))
    (print "done")))

(def update-binding-channel (chan 20))

(def d (atom nil))

(defn get-new-data [key pairs]
  (get (pairs key) "newValue"))

(defn setup-listener []
  (storage/add-events-to-channel! update-binding-channel)
  (go-loop []
    (let [[_ [changes]] (<! update-binding-channel)
          data (js->clj changes)
          is-ctrl (get-new-data "expand-parent-is-ctrl" data)
          letter (get-new-data "expand-parent-letter" data)]
      (reset! d data)
      (update-key! letter is-ctrl)
      (recur))))

(def listener-loop (atom nil))

(defn init! []
  (enable-console-print!)
  (log "CONTENT SCRIPT: init")
  (.addEventListener js/document "keyup" key-handler)
  (reset! listener-loop (setup-listener)))

;; (.removeEventListener js/document "keyup" key-handler)
