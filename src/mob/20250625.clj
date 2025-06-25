(ns mob.20250625
  (:require 
   [buddy.sign.jwt :as jwt]
   [org.httpkit.server :as http]
   [hiccup2.core :as h]
   [ring.middleware.defaults :as rmd]))

;; auth

;; options:
;;   basic auth  => cookie session
;;   form user name + password  => cookie session
;;   email tokens <--- implementing this one today
;;   oauth (login w/ google)
;;   passkeys


;; today, email token approach:
;; 
;; user enters email via form
;; handler: 
;;   post /login   {:email "...."}
;;   check if email is allowed (hardcoded set of emails), 
;;   generate a login link (jwt hmac, choosing a reasonable expiry)
;;   email the link
;; user clicks on link
;; handler: 
;;   get /auth?token=.....     
;;   verifies the token
;;   sets a session cookie with the user's email (or id, or whatever...)
;; middleware:
;;    checks if request has a session, denies if it doesn't
;;    apply the middleware to all admin endpoints

;; could do this via:
;;   encryption and decryption
;;   message signing <=== this is the approach we will take

;;  sign({:email "bob@example.com"}, "secret", expiry-date) => "token:message+stuff"
;;  verify("token", "secret", current-date) => {:email "bob@example.com"}

;; exploring buddy-sign

(def secret "108y230t9h2309th2903th")
(def token (jwt/sign {:email "thomas@example.com"
                      :exp #inst "2025-06-30"}
                     secret))
#_(jwt/unsign token secret {:now #inst "2025-07-30"})

;; web server

(defn send-mail!
  [mail]
  (let [token (jwt/sign {:email mail
                         :exp #inst "2025-06-30"}
                        secret)]
    (println {:mail mail
              :token token
              :link (str "http://localhost:8125/auth?token=" token)})))

(defn handler [request]
  (cond

    (and (= (:uri request) "/") (= (:request-method request) :get))
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str (h/html
                 [:form {:method "post" :action "/login"}
                  [:input {:type "email"
                           :required true
                           :name "mail"}]
                  [:button {:type "submit"} "submit"]]))}

    (and (= (:uri request) "/login") (= (:request-method request) :post))
    (if (= "a@example.com" (get-in request [:params :mail]))
      (do
        (send-mail! (get-in request [:params :mail]))
        ;; TODO should redirect, because this a post
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body "check your email"})
      {:status 403
       :headers {"Content-Type" "text/html"}
       :body "Unauthorized"})

    (and (= (:uri request) "/auth") (= (:request-method request) :get))
    (if-let [{:keys [email]} (try
                               (jwt/unsign (get-in request [:params :token])
                                           secret)
                               (catch Exception _
                                 nil))]
      {:status 302
       :headers {"Content-Type" "text/html"
                 "Location" "/admin"}
       :session {:email email}}
      {:status 403
       :headers {"Content-Type" "text/html"}
       :body "Malformed or expired token"})

    (and (= (:uri request) "/admin") (= (:request-method request) :get))
    ;; TODO would want middleware that extract email from session
    ;;   and denies if its not there
    ;;   so that we can re-use it on all /admin routes
    (if-let [email (get-in request [:session :email])]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (str "Welcome " email)}
      {:status 403
       :headers {"Content-Type" "text/html"}
       :body "Unauthorized"})))

(def app
  (rmd/wrap-defaults handler
                     (-> rmd/site-defaults
                         ;; consider if you want CSRF, or just rely on same-site
                         (assoc-in [:security :anti-forgery] false)
                         (assoc-in [:security :cookie-attrs :same-site] :lax)
                         ;; TODO review other defaults
                         ;;   may want to set the cookie name
                         ;;   choose how sessions are stored)))

#_(http/run-server #'app {:port 8125})

