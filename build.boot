(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "2.1.3"      :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.3"      :scope "test"]
                 [adzerk/boot-reload        "0.5.1"      :scope "test"]
                 [pandeiro/boot-http        "0.8.3"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.13"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/spec.alpha "0.1.123"]
                 [org.clojure/clojurescript "1.9.562"]
                 [binaryage/chromex "0.5.9"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl cljs-repl-env]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]])

(deftask cljs-build []
  (comp
   (cljs :ids #{"popup" "script"})
   (target)))

(deftask production []
  (task-options! cljs {:optimizations :advanced})
  (cljs-build))

(deftask run []
  (comp
   (watch)
   (cljs-repl-env :port 58491 :ids #{"popup"})
   (cljs-build)))

;; (serve :port 8000)
;; (reload)
;; (cljs-repl-env)

;; (def p (future (boot (run))))
;; (start-repl)
