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

package springfox.documentation.spring.web

import spock.lang.Specification
import spock.lang.Unroll

class ControllerNamingUtilsSpec extends Specification {

  def "Cannot instantiate this class"() {
    when:
    new ControllerNamingUtils()

    then:
    thrown(UnsupportedOperationException)
  }

  @Unroll
  def "path roots"() {
    expect:
    expected == ControllerNamingUtils.pathRoot(path)
    encoded == ControllerNamingUtils.pathRootEncoded(path)
    expected == ControllerNamingUtils.decode(encoded)
    where:
    path       | expected | encoded
    '/a/b'     | '/a'     | '/a'
    '/a/b/c/d' | '/a'     | '/a'
    'a/b'      | '/a'     | '/a'
    'a'        | '/a'     | '/a'
    '/'        | '/'      | '/'
    '//'       | '/'      | '/'
    '/{}'      | '/{}'    | '/%7B%7D'
    '/()'      | '/()'    | '/()'
  }

  def "path roots should throw exception"() {
    when:
    ControllerNamingUtils.pathRoot(path)
    then:
    thrown(IllegalArgumentException)
    where:
    path << [null, '', "", "           "]
  }

  def "decode illegal path should return path as-is"() {
    when:
    def decoded = ControllerNamingUtils.decode(path)
    then:
    Objects.equals(decoded, path)
    where:
    path << [null, '', ""]
  }

}

