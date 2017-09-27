(ns popup.popup
  (:require [popup.core :as core]
            [weasel.repl :as repl]))

;; get this from start-repl command
(comment (when-not (repl/alive?)
           (repl/connect "ws://localhost:58491")))

(core/init!)
