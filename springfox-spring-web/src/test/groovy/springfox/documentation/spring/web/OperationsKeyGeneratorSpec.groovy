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
package springfox.documentation.spring.web

import org.springframework.web.method.HandlerMethod
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.dummy.models.Example

class OperationsKeyGeneratorSpec extends Specification {
  def "Exception when param isnt of type RequestMappingContext" () {
    given:
      OperationsKeyGenerator sut = new OperationsKeyGenerator()
    when:
      sut.generate(null, null, args)
    then:
      thrown(Exception)
    where:
      args << [[], [null], [Mock(Example)], null]
  }

  def "Exception when param isnt of type RequestMappingContext using the static helper" () {
    when:
      OperationsKeyGenerator.operationKey(args)
    then:
      thrown(Exception)
    where:
      args << [[], [null], [Mock(Example)], null]
  }

  def "Generates key when param is of type RequestMappingContext" () {
    given:
      OperationsKeyGenerator sut = new OperationsKeyGenerator()
    and:
      RequestMappingContext context = Mock(RequestMappingContext)
      HandlerMethod handlerMethod = Mock(HandlerMethod)
      context.getHandlerMethod() >> handlerMethod
      handlerMethod.getMethod() >> Example.declaredMethods.find {it.name == "getFoo"}
      context.documentationContext >> new DocumentationContextBuilder(DocumentationType.SPRING_WEB)
          .genericsNaming(new DefaultGenericTypeNamingStrategy())
          .build()
      context.requestMappingPattern >> "test"
    when:
      def key = sut.generate(null, null, context)
    then:
      key == "test.getFoo.DefaultGenericTypeNamingStrategy"
  }

}
