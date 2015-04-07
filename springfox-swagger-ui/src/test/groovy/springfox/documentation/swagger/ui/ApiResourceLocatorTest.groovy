/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package springfox.documentation.swagger.ui

import spock.lang.Specification
import springfox.documentation.service.Documentation
import springfox.documentation.spring.web.DocumentationCache

class ApiResourceLocatorTest extends Specification {
  def "should find swagger api resources"() {
    given:
      Documentation documentation = Mock {
        getGroupName() >> 'group-a'
        getBasePath() >> 'some-base'
      }
      DocumentationCache documentationCache = Mock {
        all() >> [documentation]
      }

      ApiResourceLocator apiResourceLocator = new ApiResourceLocator(documentationCache)

    expect:
      SwaggerApi swaggerApi = apiResourceLocator.resources()[0]
      swaggerApi.title == 'group-a'
      swaggerApi.uri == 'some-base'
  }
}
