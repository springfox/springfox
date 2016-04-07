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

package springfox.documentation.swagger.readers.operation
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

import static com.google.common.collect.Lists.*

@Mixin([RequestMappingSupport, AuthSupport])
class OperationAuthReaderSpec extends DocumentationContextSpec {

  OperationAuthReader sut = new OperationAuthReader()

  def "should read from annotations"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(new CachingOperationNameGenerator()),
              RequestMethod.GET, dummyHandlerMethod('methodWithAuth'), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)

    then:
      operation.securityReferences.containsKey("oauth2")
      AuthorizationScope authorizationScope = operation.securityReferences.get("oauth2")[0]
      authorizationScope.getDescription() == "scope description"
      authorizationScope.getScope() == "scope"
  }

  def "should apply global auth"() {
    given:
      SecurityContext securityContext = SecurityContext.builder()
              .securityReferences(defaultAuth())
              .forPaths(PathSelectors.any())
              .build()
      plugin.securityContexts(newArrayList(securityContext))
      OperationContext operationContext = new OperationContext(new OperationBuilder(new CachingOperationNameGenerator()),
              RequestMethod.GET, dummyHandlerMethod(), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def authorizations = operationContext.operationBuilder().build().securityReferences

    then:
      def scopes = authorizations.get('oauth2')
      AuthorizationScope authorizationScope = scopes[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
  }

  def "should apply global auth when ApiOperationAnnotation exists without auth values"() {
    given:
      SecurityContext securityContext = SecurityContext.builder()
              .securityReferences(defaultAuth())
              .forPaths(PathSelectors.any())
              .build()
      plugin.securityContexts(newArrayList(securityContext))
      OperationContext operationContext = new OperationContext(new OperationBuilder(new CachingOperationNameGenerator()),
              RequestMethod.GET, dummyHandlerMethod('methodWithHttpGETMethod'), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")
    when:
      sut.apply(operationContext)
      def authorizations = operationContext.operationBuilder().build().securityReferences

    then:
      def scopes = authorizations.get("oauth2")
      AuthorizationScope authorizationScope = scopes[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
  }
}
