(ns util.storage
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! chan] :as async]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.protocols :refer [get set]]
            [chromex.ext.storage :as storage]))

;; spec this and use it in popup -> as ids of elements?
;; keys - expand parent
;; do keyword to string conversion here
;; add error handling here
(def local-storage (storage/get-local))

(defn update-data! [data]
  (set local-storage (clj->js data)))

(defn update-expand-parent! [letter is-ctrl?]
  (update-data! {"expand-parent-letter" letter
                "expand-parent-is-ctrl" is-ctrl?}))

;; adds events to a channel
(defn add-events-to-channel! [channel]
  (storage/tap-on-changed-events channel))

(defn put-data-in-callback [callback]
  (go
    (let [[[bindings] error] (<! (get local-storage (clj->js ["expand-parent-letter"
                                                              "expand-parent-is-ctrl"])))]
      (let [new-data (if error
                       (str "error! " (js->clj error))
                       (js->clj bindings))]
        (callback new-data))))
  nil)
