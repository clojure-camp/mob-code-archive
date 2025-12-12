(ns mob.20251211
  (:require 
   [sci.core :as sci]))

;; poking around with sci
;; https://github.com/babashka/SCI

;; what?

;; a clojure interpreter that runs in clojure

;; why?

;; babashka - have a precompiled sci binary than can then run "clojure" code without java
;;      faster start-up
;;      can't graalvm clojure eval (because of reflection)

;; malli - uses sci to store arbitrary validation functions in a way that is serializible and portable (clj and cljs)
;; probably chose sci over eval, because of sandboxing (ability run unstrusted code) - code run within sci can't modify the parent environment
;; size? sci might actually be a smaller cljs dependency than including eval

;; clj-kondo (linter) - speed of start up

;; cyberleague - sandboxing

;; generally, for running "clojure-ish" code that you don't know what it will be at compile time, and it might come from an untrusted source

;; could use sci and grallvm to package up a native script (to run on a system without java, and to start up quickly) -- typically, people do this with babashka to get access to the libs packaged with babashka

;; worth noting: probably avoid for performance-sensitive code, because sci (& babashka) lack precompiling and run-time optimization

(eval (read-string "(inc 1)"))

(sci/eval-string "(inc 1)")

(def x 42)
;; clojure eval will allow you to access the scope of 'x'
(eval (read-string "(inc x)"))

;; sci will not bring in the current environment for execution purposes
;; so x is not defined
(sci/eval-string "(inc x)") ;; => err

;; lets try to give it access to x, this defines 'x' in the execution
;; environment to the 'x' in our current environment
(sci/eval-string "(inc x)"
                 {:namespaces {'user {'x x}}})

(sci/eval-string "(println 3)") ;; won't work

(sci/eval-string "(println 3)"
                 {:namespaces {'user {'println println}}})


;; what about infinite loops?
;; nope not handled
;; https://github.com/babashka/sci/issues/348

;; maintaining a context:

(let [ctx (sci/init {:bindings {'x 10
                                'println println}})]
  (sci/eval-string* ctx "(defonce foo (atom x))")
  (println (-> ctx :env deref :namespaces (get 'user) (get 'foo) deref))
  (sci/eval-string* ctx "(println @foo)")
  (sci/eval-string* ctx "(swap! foo inc)")
  (println (-> ctx :env deref :namespaces (get 'user) (get 'foo) deref))
  (sci/eval-string* ctx "(println @foo)"))

(let [ctx (sci/init {:bindings {'x 10
                                'hellokitty (fn [x] (println x "meow!"))}})]
  (sci/eval-string* ctx "(defonce foo (atom x))")
  (sci/eval-string* ctx "(hellokitty @foo)")
  (sci/eval-string* ctx "(swap! foo inc)")
  (sci/eval-string* ctx "(hellokitty @foo)"))

;; classes

(sci/eval-string "(java.util.Date.)"
                 {:classes {'java.util.Date java.util.Date}})