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

package springfox.documentation.spring.web.readers.operation

import spock.lang.Unroll
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationCommandReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  private static final int CURRENT_COUNT = 3

  @Unroll("property #property expected: #expected")
  def "should set various properties based on method name or swagger annotation"() {
    given:
      OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod, CURRENT_COUNT)

    when:
      command.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation."$property" == expected
    where:
      command                         | property     | handlerMethod                              | expected
      new DefaultOperationReader()    | 'notes'      | dummyHandlerMethod()                       | null
      new DefaultOperationReader()    | 'uniqueId'   | dummyHandlerMethod()                       | 'dummyMethodUsingGET'
      new DefaultOperationReader()    | 'position'   | dummyHandlerMethod()                       | CURRENT_COUNT
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod('methodWithDeprecated') | 'true'
      new OperationDeprecatedReader() | 'deprecated' | dummyDeprecatedController()                | 'true'
      new OperationDeprecatedReader() | 'deprecated' | dummyHandlerMethod()                       | 'false'
  }

  def "Should support all documentation types"() {
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)

    where:
      sut << [new OperationDeprecatedReader(), new DefaultOperationReader()]
  }
}