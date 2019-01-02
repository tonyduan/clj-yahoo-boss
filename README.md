### A Clojure client for the Yahoo BOSS Search API

Last update: September 2014.

---

This client is a wrapper around Yahoo's API found here:
https://developer.yahoo.com/boss/search/boss_api_guide/

#### Motivation

There are plenty of Clojure APIs written around supporting Twitter,
Facebook, Alexa and the like but none for the Yahoo BOSS API. This is in
large due to the fact that very few libraries support the 'GET' call for
OAuthV1, which is required.

This libary was built in response to these issues. It is built upon the Java
Scribe OAuth library, at https://github.com/fernandezpablo85/scribe-java.

#### Leiningen

```
[tonyduan/clj-yahoo-boss "1.0.2"]
```

#### Usage

```
(use 'yboss.api)
(query-yboss "your query here" :limitedweb oauth-key oauth-secret)
```

This call will return a list of query results, formatted as hashmaps.

At the moment, the following query types are supported:
`#{:limitedweb :web :images :news :spelling}`

#### License

This library is available under the MIT license.
