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

import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationTagsReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  def "should have correct tags"() {
    given:
      OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)

    and:
      OperationTagsReader sut = new OperationTagsReader(new DefaultTagsProvider())

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      operation.tags.containsAll([group])

    where:
      handlerMethod                                                        | group
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | "dummy-class"
      dummyControllerHandlerMethod()                                       | "dummy-controller"
  }
}
