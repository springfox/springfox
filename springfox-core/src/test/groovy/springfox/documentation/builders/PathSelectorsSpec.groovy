/*
 *
 *  Copyright 2015-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.builders

import spock.lang.Specification

import static springfox.documentation.builders.PathSelectors.*

class PathSelectorsSpec extends Specification {
  def "Static types cannot be instantiated" () {
    when:
      PathSelectors.newInstance();
    then:
      thrown(UnsupportedOperationException)
  }

  def "any predicate matches all RequestHandlers" () {
    expect:
      PathSelectors.any().test("asdasdas")
  }

  def "none predicate matches no RequestHandlers" () {
    expect:
      !none().test("asdasdasdasd")
  }

  def "matches ant expressions"() {
    expect:
    shouldMatch == ant(included).test(pathToMatch)

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
    shouldMatch == regex(included).test(patternConditions)

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
