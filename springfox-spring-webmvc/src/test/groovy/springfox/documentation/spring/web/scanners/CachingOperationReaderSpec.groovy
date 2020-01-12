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
package springfox.documentation.spring.web.scanners

import com.fasterxml.classmate.TypeResolver

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.service.Operation
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.paths.Paths
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.spring.web.readers.operation.OperationReader

class CachingOperationReaderSpec
    extends DocumentationContextSpec
    implements TypesForTestingSupport, RequestMappingSupport {
  def "Implementation caches the invocations"() {
    given:
    RequestMappingInfo requestMappingInfo = requestMappingInfo('/anyPath')
    def methodResolver = new HandlerMethodResolver(new TypeResolver())

    def context = documentationContext()
    def requestMappingContext = new RequestMappingContext(
        "0",
        context,
        new WebMvcRequestHandler(Paths.ROOT, methodResolver,
            requestMappingInfo,
            dummyHandlerMethod("methodWithConcreteResponseBody")))
    def mock = Mock(OperationReader) {
      read(requestMappingContext) >> [anOperation()]
    }
    when:
    def sut = new CachingOperationReader(mock)
    then:
    sut.read(requestMappingContext) == sut.read(requestMappingContext)
  }

  def anOperation() {
    Mock(Operation)
  }
}
