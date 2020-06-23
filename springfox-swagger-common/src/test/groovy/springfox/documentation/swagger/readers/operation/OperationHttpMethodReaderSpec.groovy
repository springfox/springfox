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

import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationHttpMethodReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  def "should return api method annotation when present"() {

    given:
      OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)

      OperationHttpMethodReader sut = new OperationHttpMethodReader();
    when:
      sut.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()

    then:
      operation.method == expected
    and:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
    where:
      currentHttpMethod  | handlerMethod                                     | expected
      RequestMethod.GET  | dummyHandlerMethod()                              | HttpMethod.GET
      RequestMethod.PUT  | dummyHandlerMethod()                              | HttpMethod.GET
      RequestMethod.POST | dummyHandlerMethod('methodWithHttpGETMethod')     | HttpMethod.GET
      RequestMethod.POST | dummyHandlerMethod('methodWithInvalidHttpMethod') | HttpMethod.GET
  }
}
