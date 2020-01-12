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

package springfox.documentation.swagger.readers.operation

import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.util.stream.Stream

import static java.util.Collections.*
import static java.util.stream.Collectors.*

class SwaggerMediaTypeReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  def "handler method should override spring media types"() {
    RequestMappingInfo requestMappingInfo =
        requestMappingInfo('/somePath',
            [
                'consumesRequestCondition': consumesRequestCondition(['application/json'] as String[]),
                'producesRequestCondition': producesRequestCondition(['application/json'] as String[])
            ]
        )
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod, 0, requestMappingInfo, RequestMethod.POST)
    operationContext.operationBuilder().method(HttpMethod.POST)

    when:
    def sut = new SwaggerMediaTypeReader()
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.consumes == expectedConsumes
    operation.produces == expectedProduces

    and:
    !sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)

    where:
    expectedConsumes                                  | expectedProduces                                  | handlerMethod
    singleton('application/xml')                     | new HashSet<>()                                    | dummyHandlerMethod('methodWithXmlConsumes')
    new HashSet<>()                                    | singleton('application/xml')                     | dummyHandlerMethod('methodWithXmlProduces')
    singleton('application/xml')                     | singleton('application/json')                    | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
    singleton('application/xml')                     | singleton('application/xml')                     | dummyHandlerMethod('methodWithBothXmlMediaTypes')
    Stream.of('application/xml', 'application/json').collect(toSet()) | Stream.of('application/xml', 'application/json').collect(toSet()) | dummyHandlerMethod('methodWithMultipleMediaTypes')

  }
}
