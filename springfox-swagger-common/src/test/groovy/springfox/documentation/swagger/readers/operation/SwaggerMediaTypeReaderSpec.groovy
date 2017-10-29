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

import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec


@Mixin([RequestMappingSupport])
class SwaggerMediaTypeReaderSpec extends DocumentationContextSpec {
  def "handler method should override spring media types"() {
      RequestMappingInfo requestMappingInfo =
            requestMappingInfo('/somePath',
                    [
                            'consumesRequestCondition': consumesRequestCondition(['application/json'] as String[]),
                            'producesRequestCondition': producesRequestCondition(['application/json'] as String[])
                    ]
            )
      OperationContext operationContext =
        operationContext(context(), handlerMethod, 0, requestMappingInfo)

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
      expectedConsumes                            | expectedProduces                             | handlerMethod
      Collections.singleton('application/xml')    | new HashSet()                                | dummyHandlerMethod('methodWithXmlConsumes')
      new HashSet()                               | Collections.singleton('application/xml')     | dummyHandlerMethod('methodWithXmlProduces')
      Collections.singleton('application/xml')    | Collections.singleton('application/json')    | dummyHandlerMethod ('methodWithMediaTypeAndFile', MultipartFile)
      Collections.singleton('application/xml')    | Collections.singleton('application/xml')     | dummyHandlerMethod  ('methodWithBothXmlMediaTypes')
      new HashSet(Arrays.asList('application/xml', 'application/json')) | new HashSet(Arrays.asList('application/xml', 'application/json')) | dummyHandlerMethod('methodWithMultipleMediaTypes')

  }
}
