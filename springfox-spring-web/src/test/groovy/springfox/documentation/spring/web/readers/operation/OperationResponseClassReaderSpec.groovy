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
import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spi.service.contexts.OperationContext

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class OperationResponseClassReaderSpec extends DocumentationContextSpec {
  OperationResponseClassReader sut
  
  def setup() {
    def typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())

    sut = new OperationResponseClassReader(new TypeResolver(), typeNameExtractor)
  }
  
  def "Should support all documentation types"() {
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }
  def "should have correct response class"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      if (operation.responseModel.collection) {
        assert expectedClass == String.format("%s[%s]", operation.responseModel.type, operation.responseModel.itemType)
      } else {
        assert expectedClass == operation.responseModel.type
      }

    where:
      handlerMethod                                                        | expectedClass
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'
      dummyHandlerMethod('methodWithListOfBusinesses')                     | 'List[BusinessModel]'
  }

}
