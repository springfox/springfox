/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class OperationResponseClassReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  OperationResponseClassReader sut
  
  def setup() {
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    def typeNameExtractor = new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    sut = new OperationResponseClassReader(defaultSchemaPlugins(), new JacksonEnumTypeDeterminer(), typeNameExtractor)
  }
  
  def "Should support all documentation types"() {
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "should have correct response class"() {
    given:
      OperationContext operationContext = operationContext(documentationContext(), handlerMethod)
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
    then:
      if (operation.responseModel.collection) {
        assert expectedClass == String.format("%s[%s]", operation.responseModel.type, operation.responseModel.itemType)
      } else {
        assert expectedClass == operation.responseModel.type
        if ("Map".equals(operation.responseModel.type)) {
          assert operation.responseModel.isMap()
          assert !isNullOrEmpty(operation.responseModel.itemType)
        }
        if (allowableValues == null) {
          assert operation.responseModel.getAllowableValues() == null
        } else {
          assert allowableValues == operation.responseModel.getAllowableValues().values
        }
      }

    where:
      handlerMethod                                                        | expectedClass              | allowableValues
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'            |  null
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'            |  null
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'        | null
      dummyHandlerMethod('methodWithListOfBusinesses')                     | 'List[BusinessModel]'      | null
      dummyHandlerMethod('methodWithMapReturn')                            | 'Map«string,BusinessModel»'| null
      dummyHandlerMethod('methodWithEnumResponse')                         | 'string'                   | ['ONE', 'TWO']
      dummyHandlerMethod('methodWithByteArray')                            | 'Array[byte]'              | ['ONE', 'TWO']
  }

}
