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

import org.springframework.mock.env.MockEnvironment
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationCommandReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  private static final int CURRENT_COUNT = 3
  @Shared
  def descriptions = new DescriptionResolver(new MockEnvironment())

  @Unroll("property #property expected: #expected")
  def "should set various properties based on method name or swagger annotation"() {
    given:
      OperationContext operationContext =
          operationContext(documentationContext(), handlerMethod, CURRENT_COUNT)

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation."$property" == expected
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    where:
      sut                                      | property   | handlerMethod                            | expected
      new OperationSummaryReader(descriptions) | 'summary'  | dummyHandlerMethod('methodWithSummary')  | 'summary'
      new OperationHiddenReader()              | 'hidden'   | dummyHandlerMethod('methodThatIsHidden') | true
      new OperationHiddenReader()              | 'hidden'   | dummyHandlerMethod('dummyMethod')        | false
      new OperationNotesReader(descriptions)   | 'notes'    | dummyHandlerMethod('methodWithNotes')    | 'some notes'
      new OperationPositionReader()            | 'position' | dummyHandlerMethod('methodWithPosition') | 5
  }
}
