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

(defn update-expand-parent [letter is-ctrl?]
  (update-data! {"expand-parent-letter" letter
                "expand-parent-is-ctrl" is-ctrl?}))

;; adds events to a channel
(defn add-events-to-channel! [channel]
  (storage/tap-on-changed-events channel))



(comment 
  (defn test-storage! []
    (let [local-storage (storage/get-local)]
      (set local-storage #js {"key1" "string"
                              "key2" #js [1 2 3]
                              "key3" true
                              "key4" nil})
      (go
        (let [[[items] error] (<! (get local-storage))]
          (if error
            (error "fetch all error:" error)
            (log "fetch all:" items))))
      (go
        (let [[[items] error] (<! (get local-storage "key1"))]
          (if error
            (error "fetch key1 error:" error)
            (log "fetch key1:" items))))
      (go
        (let [[[items] error] (<! (get local-storage #js ["key2" "key3"]))]
          (if error
            (error "fetch key2 and key3 error:" error)
            (log "fetch key2 and key3:" items)))))))
