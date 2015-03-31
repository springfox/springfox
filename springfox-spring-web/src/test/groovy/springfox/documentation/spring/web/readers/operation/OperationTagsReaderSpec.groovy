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

package springfox.documentation.spring.web.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Shared
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.spi.service.ResourceGroupingStrategy
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.SpringGroupingStrategy
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class OperationTagsReaderSpec extends DocumentationContextSpec {
  @Shared ResourceGroupingStrategy groupingStrategy = new SpringGroupingStrategy()
  
  def "should have correct tags"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    and:
      OperationTagsReader sut = new OperationTagsReader(groupingStrategy)

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
