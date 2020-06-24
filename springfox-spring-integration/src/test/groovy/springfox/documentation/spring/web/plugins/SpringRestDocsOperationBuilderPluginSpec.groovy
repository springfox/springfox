/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
package springfox.documentation.spring.web.plugins

import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification
import springfox.documentation.RequestHandler
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

class SpringRestDocsOperationBuilderPluginSpec extends Specification {

  RequestHandler requestHandler = Mock() {
    getName() >> "toLowerGateway" // two matching snippets in test resources
  }

  DocumentationContext documentationContext = Mock()

  SpringRestDocsOperationBuilderPlugin sut = new SpringRestDocsOperationBuilderPlugin()
  def operationBuilder = new OperationBuilder(new CachingOperationNameGenerator())

  OperationContext operationContext = new OperationContext(
      operationBuilder,
      RequestMethod.GET,
      new RequestMappingContext(
          "0",
          documentationContext,
          requestHandler),
      0)

  def "Collects examples"() {
    given:
    requestHandler // need to mock the name

    when:
    sut.apply(operationContext)
    def operation = operationBuilder.build()

    then:
    2 == operation.responses[0].examples.size()
  }


}
