package springfox.documentation.builders
import spock.lang.Specification

import static PathSelectors.*
import static RequestHandlerSelectors.*

class PathSelectorsSpec extends Specification {
  def "Static types cannot be instantiated" () {
    when:
      PathSelectors.newInstance();
    then:
      thrown(UnsupportedOperationException)
  }

  def "any predicate matches all RequestHandlers" () {
    expect:
      PathSelectors.any().apply("asdasdas")
  }

  def "none predicate matches no RequestHandlers" () {
    expect:
      !none().apply("asdasdasdasd")
  }

  def "matches ant expressions"() {
    expect:
    shouldMatch == ant(included).apply(pathToMatch)

    where:
    included               | pathToMatch                    | shouldMatch
    ''                     | '/path'                         | false
    'sdfs'                 | '/path'                         | false
    '/path'                | '/path'                         | true
    '/?ath'                | '/path'                         | true
    '/*ath'                | '/path'                         | true
    '/**path'              | '/path'                         | true
    '/path/'               | '/path'                         | false
    '/**path'              | '/path'                         | true
    '/**'                  | '/path'                         | true
    '/businesses/accounts' | '/businesses/accounts'          | true
    '/**'                  | '/businesses/accounts'          | true
    '/*/accounts'          | '/businesses/accounts'          | true
    '/*/accounts'          | '/businesses/accounts'          | true
    '/**/accounts'         | '/businesses/accounts'          | true
    '/**businesses/**'     | '/businesses/accounts'          | true
    '/**'                  | '/businesses/accounts/balance'  | true
    '/?ath'                | '/path'                         | true
    '/path'                | '/path'                         | true
    '/notAMatch'           | '/path'                         | false
    '/path'                | '/path'                         | true
    '/doNotMatch'          | '/path'                         | false
    '/both'                | '/path'                         | false
  }

  def "matches regex expressions"() {
    given:
    expect:
    shouldMatch == regex(included).apply(patternConditions)

    where:
    patternConditions | included      | shouldMatch
    '/businesses'     | '.*?'         | true
    '/businesses'     | '.*?'         | true
    '/businesses'     | "/bus.*"      | true
    '/businesses'     | '^/bus.*'     | true
    '/businesses'     | '/businesses' | true
    '/businesses'     | '/accounts'   | false
    '/businesses'     | '/acc.*'      | false
  }
}
