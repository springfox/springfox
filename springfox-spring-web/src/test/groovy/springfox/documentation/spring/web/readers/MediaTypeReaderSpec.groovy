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

package springfox.documentation.spring.web.readers

import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Shared
import spock.lang.Unroll
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.spring.web.scanners.MediaTypeReader

import static com.google.common.collect.Sets.*

@Mixin([RequestMappingSupport])
class MediaTypeReaderSpec extends DocumentationContextSpec {
  MediaTypeReader sut
  @Shared Set<String> emptySet = newHashSet()
  def setup() {
    sut = new MediaTypeReader(new TypeResolver())
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
      OperationContext operationContext = new OperationContext(new OperationBuilder(new CachingOperationNameGenerator()),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo,
              context(), "")
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.consumes == newHashSet(consumes)
      operation.produces == newHashSet(produces)

    where:
      consumes                                            | produces                         | handlerMethod
      ['application/json'] as String[]                    | ['application/json'] as String[] | dummyHandlerMethod()
      ['application/json'] as String[]                    | ['application/xml'] as String[]  | dummyHandlerMethod()
      ['multipart/form-data'] as String[]                 | ['application/json'] as String[] | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
      ['application/json', 'application/xml'] as String[] | ['application/xml'] as String[]  | dummyHandlerMethod()
  }


}
