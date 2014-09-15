(defproject tonyduan/clj-yahoo-boss "1.0.2"

  :description "A Clojure client for the Yahoo BOSS Search API"
  :url "https://github.com/tonyduan/clj-yahoo-boss"

  :license {
    :name "MIT License"
    :url "http://opensource.org/licenses/MIT"}

  :repositories [
    ["scribe-java-mvn-repo" "https://raw.github.com/fernandezpablo85/scribe-java/mvn-repo"]]

  :dependencies [
    [org.clojure/clojure "1.6.0"]
    [org.scribe/scribe "1.3.6"]
    [cheshire "5.3.1"]
    [enlive "1.1.5"]]

  :scm {:name "git"
    :url "https://github.com/tonyduan/clj-yahoo-boss"})
