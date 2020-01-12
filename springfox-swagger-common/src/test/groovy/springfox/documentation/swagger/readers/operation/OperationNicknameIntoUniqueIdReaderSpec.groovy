/*
 *
 *  Copyright 2016 the original author or authors.
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

import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationNicknameIntoUniqueIdReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  def "should set various unique operation id based on swagger annotation"() {
    given:
      OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)
    and:
      def sut = new OperationNicknameIntoUniqueIdReader()
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.uniqueId == expected
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    where:
      handlerMethod                                 | expected
      dummyHandlerMethod('methodWithHttpGETMethod') | 'nullUsingGET'
      dummyHandlerMethod('methodWithNickName')      | 'unique'
  }
}
