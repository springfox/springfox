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

import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationPositionReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {

  def "should have correct api position using swagger reader"() {
    given:
      OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod, contextCount)
      OperationPositionReader operationPositionReader = new OperationPositionReader();

    when:
      operationPositionReader.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.position == expectedCount
    where:
      handlerMethod                            | contextCount  | expectedCount
      dummyHandlerMethod()                     | 2             | 0
      dummyHandlerMethod('methodWithPosition') | 3             | 5
  }
}
