/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.mixins.AuthSupport
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import static java.util.Collections.*

class OperationAuthReaderSpec
    extends DocumentationContextSpec
    implements AuthSupport, RequestMappingSupport {

  OperationAuthReader sut = new OperationAuthReader()

  def "should read from annotations"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithAuth'))

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
      plugin.securityContexts(singletonList(securityContext))
      OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod())

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
      plugin.securityContexts(singletonList(securityContext))
      OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithHttpGETMethod'))

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
