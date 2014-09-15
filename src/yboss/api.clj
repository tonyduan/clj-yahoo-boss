(ns yboss.api
  (:require
    [cheshire.core :as json]
    [clojure.walk :refer [keywordize-keys]]
    [net.cgrand.enlive-html :as enlive])
  (:import
    [org.scribe.builder ServiceBuilder]
    [org.scribe.builder.api DefaultApi10a]
    [org.scribe.model OAuthConstants OAuthRequest Verb]))

(def valid-query-types
  "Valid query type parameters for searching through Yahoo BOSS."
  #{:limitedweb :web :images :news :spelling})

(defn- yboss-search-endpoint [query query-type]
  "Generate the necessary endpoint given a query and its type."
  (str "https://yboss.yahooapis.com/ysearch/" (name query-type) "?q=" query))

(defn- generate-yboss-api [endpt]
  "Generate the API service provider to send to a specific endpoint."
  (proxy [DefaultApi10a] []
    (getAccessTokenVerb [] nil)
    (getRequestTokenVerb [] Verb/GET)
    (getRequestTokenEndpoint [] endpt)
    (getAccessTokenEndpoint [] nil)
    (getAuthorizationUrl [requestToken] nil)))

(defn- get-oauth-service [endpt oauth-key oauth-secret]
  "Create an oauth service through which we can exceute queries."
  (let [builder (doto (ServiceBuilder.)
                  (.provider (generate-yboss-api endpt))
                  (.apiKey oauth-key)
                  (.apiSecret oauth-secret))]
    (.build builder)))

(defn- clean-arguments [string]
  "Need to prevent clean a query by encoding special reserved characters into
   the proper values. See the link below.
   https://developer.yahoo.com/boss/search/boss_api_guide/reserve_chars_esc_val"
  ((apply comp
      (for [mapping {
        "?" "%3F", "&" "%26", ";" "%3B", ":" "%3A", "@" "%40", "`" "%60",
        "," "%2C", "$" "%24", "=" "%3D", " " "%20", "/" "%2F", "\"" "%22",
        "+" "%2B", "#" "%23", "*" "%2A", "<" "%3C", ">" "%3E", "{" "%7B",
        "}" "%7D", "|" "%7C", "[" "%5B", "]" "%5D", "^" "%5E", "\\" "%5C",
        "(" "%28", ")" "%29", }]
          #(.replace % (key mapping) (val mapping)))) string))

(defn- extract-html-text [raw-html]
  "Extract only the text from an HTML snippet."
  (clojure.string/join (map enlive/text (enlive/html-snippet raw-html))))

(defn- clean-search [search]
  "Clean a search result by reassocing HTML elements."
  (-> search
      (assoc
        :title       (extract-html-text (:title search))
        :description (extract-html-text (:abstract search)))
      (dissoc :abstract :dispurl :clickurl :date)))

(defn query-yboss [query query-type oauth-key oauth-secret]
  "Execute a query with a given key and secret, or return nil."
  (when (contains? valid-query-types query-type)
    (let [endpt   (yboss-search-endpoint (clean-arguments query) query-type)
          service (get-oauth-service endpt oauth-key oauth-secret)
          token   (OAuthConstants/EMPTY_TOKEN)
          req     (OAuthRequest. Verb/GET endpt)]
      (.signRequest service token req)
      (->> (.send req)
           (.getBody)
           (json/parse-string)
           (keywordize-keys)
           :bossresponse
           query-type
           :results
           (map clean-search)))))