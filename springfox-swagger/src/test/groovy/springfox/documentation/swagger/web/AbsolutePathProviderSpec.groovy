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

package springfox.documentation.swagger.web

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.spring.web.AbstractPathProvider
import springfox.documentation.spring.web.mixins.RequestMappingSupport

import javax.servlet.ServletContext

@Mixin(RequestMappingSupport)
class AbsolutePathProviderSpec extends Specification {

   def "assert urls"() {
      given:
        AbsolutePathProvider provider = new AbsolutePathProvider(servletContext)

      expect:
        provider.applicationPath() == expectedAppPath
        provider.getDocumentationPath() == expectedDocPath

      where:
        servletContext   | expectedAppPath                      | expectedDocPath
        servletContext() | "http://localhost:8080/context-path" | "http://localhost:8080/context-path/api-docs"
        mockContext("")  | "http://localhost:8080"              | "http://localhost:8080/api-docs"

   }

  @Unroll
  def "Absolute paths"() {
    given:
      AbstractPathProvider provider = new AbsolutePathProvider(servletContext())

    expect:
      provider.getApplicationBasePath() == expectedAppBase
      provider.getResourceListingPath(groupName, apiDeclaration) == expectedDoc

    where:
      groupName       | apiDeclaration     | expectedAppBase                      | expectedDoc
      'default'       | 'api-declaration'  | "http://localhost:8080/context-path" |  "http://localhost:8080/context-path/api-docs/default/api-declaration"
      'somethingElse' | 'api-declaration2' | "http://localhost:8080/context-path" |  "http://localhost:8080/context-path/api-docs/somethingElse/api-declaration2"

  }

   private mockContext(String path) {
      [getContextPath: { return path }] as ServletContext
   }
}
