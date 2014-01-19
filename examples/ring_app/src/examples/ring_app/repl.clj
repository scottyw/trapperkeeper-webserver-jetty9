(ns examples.ring-app.repl
  (:require [puppetlabs.trapperkeeper.services.webserver.jetty9-service :refer [jetty9-service]]
            [examples.ring-app.example-services :refer [count-service bert-service ernie-service]]
            [puppetlabs.trapperkeeper.core :as tk]
            [puppetlabs.trapperkeeper.app :as tka]
            [clojure.tools.namespace.repl :refer (refresh)]))

;; TODO: docs on what this ns is

(def system nil)

(defn init []
  (alter-var-root #'system
    (fn [_] (let [app (tk/build-app
                        [jetty9-service count-service bert-service ernie-service]
                        {:global    {:logging-config "examples/ring_app/logback.xml"}
                         :webserver {:port 8080}
                         :example   {:ernie-url-prefix "/ernie"}})]
              (tka/init app)))))

(defn start []
  (alter-var-root #'system tka/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s (tka/stop s)))))

(defn go []
  (init)
  (start))

(defn context []
  @(tka/app-context system))

(defn print-context []
  (clojure.pprint/pprint (context)))

(defn reset []
  (stop)
  (refresh :after 'examples.ring-app.repl/go))
