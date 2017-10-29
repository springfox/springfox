/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.scanners.MediaTypeReader


@Mixin([RequestMappingSupport])
class MediaTypeReaderSpec extends DocumentationContextSpec {
  MediaTypeReader sut

  @Shared
  Set<String> emptySet = new HashSet()

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
        operationContext(context(), handlerMethod, 0, requestMappingInfo)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.consumes == new HashSet(Arrays.asList(consumes))
    operation.produces == new HashSet(Arrays.asList(produces))

    where:
    consumes                                            | produces                         | handlerMethod
    ['application/json'] as String[]                    | ['application/json'] as String[] | dummyHandlerMethod()
    ['application/json'] as String[]                    | ['application/xml'] as String[]  | dummyHandlerMethod()
    ['multipart/form-data'] as String[]                 | ['application/json'] as String[] | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
    ['application/json', 'application/xml'] as String[] | ['application/xml'] as String[]  | dummyHandlerMethod()
  }

  @Unroll
  def "should only set default 'application/json' consumes if no consumes is set for the operation and document context"() {
    given:
    contextBuilder.consumes(new HashSet(Arrays.asList(documentConsumes)))
    RequestMappingInfo requestMappingInfo =
        requestMappingInfo('/somePath',
            [
                'consumesRequestCondition': consumesRequestCondition(operationConsumes)
            ]
        )
    OperationContext operationContext =
        operationContext(context(), dummyHandlerMethod(), 0, requestMappingInfo)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.consumes == new HashSet(Arrays.asList(expectedOperationConsumes))

    where:
    documentConsumes                | operationConsumes               | expectedOperationConsumes
    [] as String[]                  | [] as String[]                  | ['application/json'] as String[]
    ['application/xml'] as String[] | [] as String[]                  | [] as String[]
    [] as String[]                  | ['application/xml'] as String[] | ['application/xml'] as String[]
  }

  @Unroll
  def "should only set default '*/*' produces if no produces is set for the operation and document context"() {
    given:
    contextBuilder.produces(new HashSet(Arrays.asList(documentProduces)))
    RequestMappingInfo requestMappingInfo =
        requestMappingInfo('/somePath',
            [
                'producesRequestCondition': producesRequestCondition(operationProduces)
            ]
        )
    OperationContext operationContext =
        operationContext(context(), dummyHandlerMethod(), 0, requestMappingInfo)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.produces == new HashSet(Arrays.asList(expectedOperationProduces))

    where:
    documentProduces                | operationProduces               | expectedOperationProduces
    [] as String[]                  | [] as String[]                  | ['*/*'] as String[]
    ['application/xml'] as String[] | [] as String[]                  | [] as String[]
    [] as String[]                  | ['application/xml'] as String[] | ['application/xml'] as String[]
  }

}
