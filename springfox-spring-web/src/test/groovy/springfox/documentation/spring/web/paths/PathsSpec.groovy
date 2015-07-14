/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.spring.web.paths
import spock.lang.Specification

import static springfox.documentation.spring.web.paths.Paths.*

class PathsSpec extends Specification {
  def "maybe chomping the leading slash works"() {
    expect:
    maybeChompLeadingSlash(input) == expected

    where:
    input           || expected
    "/test"         || "test"
    "test"          || "test"
    "/test/test2"   || "test/test2"
    "/test/test2/"  || "test/test2/"
    "/test/@test2/" || "test/@test2/"
    ""              || ""
    null            || null
  }

  def "maybe chomping the trailing slash works"() {
    expect:
      maybeChompTrailingSlash(input) == expected

    where:
      input           || expected
      "/test"         || "/test"
      "test"          || "test"
      "/test/test2"   || "/test/test2"
      "/test/test2/"  || "/test/test2"
      "/test/@test2/" || "/test/@test2"
      ""              || ""
      null            || null
  }

  def "extracting first path segment"() {
    expect:
    firstPathSegment(input) == expected

    where:
    input           || expected
    "/test"         || "/test"
    "test"          || "test"
    "/test/test2"   || "/test"
    "/test/test2/"  || "/test"
    "/test/@test2/" || "/test"
    "/test@/test2/" || "/test"
    "/tes\nt/test2/"|| "/tes"
    "\n/tes\nt/test2/"|| "\n/tes\nt/test2/"
    ""              || ""
    null            || null
  }

  def "Splitting on camel case"() {
    expect:
      splitCamelCase(input, "-") == expected

    where:
      input           || expected
      "firstSecond"   || "first-Second"
      "FirstSecond"   || "First-Second"
      "FirstNSecond"  || "First-N-Second"
      "First"         || "First"
      ""              || ""
      null            || ""
  }

  def "Cannot instantiate HandlerMethodReturnTypes helper class" () {
    when:
      new Paths()
    then:
      thrown(UnsupportedOperationException)
  }
}
