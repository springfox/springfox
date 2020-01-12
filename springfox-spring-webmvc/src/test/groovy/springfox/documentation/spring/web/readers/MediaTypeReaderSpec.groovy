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

package springfox.documentation.spring.web.readers

import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Unroll
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.MediaTypeReader

import java.util.stream.Stream

import static java.util.stream.Collectors.*

class MediaTypeReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  MediaTypeReader sut

  def setup() {
    sut = new MediaTypeReader()
  }

  @Unroll
  def "should read media types"() {

    given:
    RequestMappingInfo requestMappingInfo =
        requestMappingInfo('/somePath',
            [
                'consumesRequestCondition': consumesRequestCondition(consumes),
                'producesRequestCondition': producesRequestCondition(produces)
            ]
        )
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod, 0, requestMappingInfo, httpMethod)
    operationContext.operationBuilder().method(HttpMethod.valueOf(httpMethod.toString()))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.consumes == Stream.of(consumes).collect(toSet())
    operation.produces == Stream.of(produces).collect(toSet())

    where:
    consumes                                            | produces                         | httpMethod            | handlerMethod
    [] as String[]                                      | ['application/json'] as String[] | RequestMethod.GET     | dummyHandlerMethod()
    [] as String[]                                      | ['application/json'] as String[] | RequestMethod.DELETE  | dummyHandlerMethod()
    ['application/json'] as String[]                    | ['application/xml'] as String[]  | RequestMethod.POST    | dummyHandlerMethod()
    ['application/json'] as String[]                    | ['application/xml'] as String[]  | RequestMethod.PUT     | dummyHandlerMethod()
    ['application/json'] as String[]                    | ['application/xml'] as String[]  | RequestMethod.PATCH   | dummyHandlerMethod()
    ['application/json'] as String[]                    | ['application/xml'] as String[]  | RequestMethod.OPTIONS | dummyHandlerMethod()
    ['application/json'] as String[]                    | ['application/xml'] as String[]  | RequestMethod.HEAD    | dummyHandlerMethod()
    ['multipart/form-data'] as String[]                 | ['application/json'] as String[] | RequestMethod.GET     | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
    ['application/json', 'application/xml'] as String[] | ['application/xml'] as String[]  | RequestMethod.POST    | dummyHandlerMethod()
  }

  @Unroll
  def "should only set default 'application/json' consumes if no consumes is set and operation is not GET/DELETE"() {
    given:
    contextBuilder.consumes(Stream.of(documentConsumes).collect(toSet()))
    RequestMappingInfo requestMappingInfo =
        requestMappingInfo('/somePath',
            [
                'consumesRequestCondition': consumesRequestCondition(operationConsumes)
            ]
        )
    OperationContext operationContext =
        operationContext(
            documentationContext(),
            dummyHandlerMethod(),
            0,
            requestMappingInfo,
            RequestMethod.valueOf(httpMethod.toString()))
    operationContext.operationBuilder().method(httpMethod)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.consumes == Stream.of(expectedOperationConsumes).collect(toSet())

    where:
    documentConsumes                | operationConsumes               | httpMethod      | expectedOperationConsumes
    [] as String[]                  | [] as String[]                  | HttpMethod.POST | ['application/json'] as String[]
    ['application/xml'] as String[] | [] as String[]                  | HttpMethod.POST | [] as String[]
    [] as String[]                  | ['application/xml'] as String[] | HttpMethod.POST | ['application/xml'] as String[]
    [] as String[]                  | [] as String[]                  | HttpMethod.GET  | [] as String[]
    ['application/xml'] as String[] | [] as String[]                  | HttpMethod.GET  | [] as String[]
    [] as String[]                  | ['application/xml'] as String[] | HttpMethod.GET  | [] as String[]
  }

  @Unroll
  def "should only set default '*/*' produces if no produces is set for the operation and document context"() {
    given:
    contextBuilder.produces(Stream.of(documentProduces).collect(toSet()))
    RequestMappingInfo requestMappingInfo =
        requestMappingInfo('/somePath',
            [
                'producesRequestCondition': producesRequestCondition(operationProduces)
            ]
        )
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod(), 0, requestMappingInfo)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.produces == Stream.of(expectedOperationProduces).collect(toSet())

    where:
    documentProduces                | operationProduces               | expectedOperationProduces
    [] as String[]                  | [] as String[]                  | ['*/*'] as String[]
    ['application/xml'] as String[] | [] as String[]                  | [] as String[]
    [] as String[]                  | ['application/xml'] as String[] | ['application/xml'] as String[]
  }

}
