/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.documentation.spring.web.readers

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.HttpMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.Operation
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.ApiOperationReader
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.spring.web.readers.operation.DefaultOperationReader
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver

import static com.google.common.collect.Lists.*
import static org.springframework.web.bind.annotation.RequestMethod.*

@Mixin([RequestMappingSupport, AuthSupport, ServicePluginsSupport])
class ApiOperationReaderSpec extends DocumentationContextSpec {
  ApiOperationReader sut
  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def setup() {
    SecurityContext securityContext = SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.regex(".*"))
            .build()
    plugin.securityContexts(newArrayList(securityContext))
    sut = new ApiOperationReader(customWebPlugins([], [], [new DefaultOperationReader()]), new CachingOperationNameGenerator())
  }

  def "Should generate default operation on handler method without swagger annotations"() {

    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [
                      patternsRequestCondition      : patternsRequestCondition('/doesNotMatterForThisTest', '/somePath/{businessId:\\d+}'),
                      requestMethodsRequestCondition: requestMethodsRequestCondition(PATCH, POST)
              ]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()

      RequestMappingContext context = new RequestMappingContext(context(),
              new WebMvcRequestHandler(methodResolver,
                  requestMappingInfo,
                  handlerMethod))
    when:
      def operations = sut.read(context)

    then:
      Operation apiOperation = operations[0]
      apiOperation.getMethod() == HttpMethod.PATCH
      apiOperation.getSummary() == handlerMethod.method.name
      apiOperation.getNotes() == null
      apiOperation.getUniqueId() == handlerMethod.method.name + "Using" + PATCH.toString()
      apiOperation.getPosition() == 0
      apiOperation.getSecurityReferences().size() == 0

      def secondApiOperation = operations[1]
      secondApiOperation.position == 1
  }
}
