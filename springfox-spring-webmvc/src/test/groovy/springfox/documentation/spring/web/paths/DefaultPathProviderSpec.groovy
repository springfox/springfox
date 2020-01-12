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
import springfox.documentation.spring.web.mixins.RequestMappingSupport

import javax.servlet.ServletContext

class DefaultPathProviderSpec extends Specification implements RequestMappingSupport {

  def "relative paths"() {
    given:
    ServletContext servletContext = Mock(ServletContext)
    servletContext.contextPath >> "/"
    DefaultPathProvider provider = new DefaultPathProvider()

    expect:
    provider.getResourceListingPath('default', 'api-declaration') == "/default/api-declaration"
  }


  def "should never return a path with duplicate slash"() {
    setup:
    DefaultPathProvider swaggerPathProvider = new DefaultPathProvider()

    when:
    String path = swaggerPathProvider.getResourceListingPath('/a', '/b')
    String opPath = swaggerPathProvider.getOperationPath('//a/b')

    then:
    path == '/a/b'
    opPath == path
  }

  def "should replace slashes"() {
    expect:
    Paths.removeAdjacentForwardSlashes(input) == expected

    where:
    input             | expected
    '//a/b'           | '/a/b'
    '//a//b//c'       | '/a/b/c'
    'http://some//a'  | 'http://some/a'
    'https://some//a' | 'https://some/a'
  }
}
