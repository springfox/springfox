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

import com.fasterxml.classmate.TypeResolver
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import spock.lang.Unroll
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport

class SwaggerOperationResponseClassReaderSpec
    extends DocumentationContextSpec
    implements SwaggerPluginsSupport,
        RequestMappingSupport {

  @Unroll
  def "should have correct response class"() {
    given:
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    def typeNameExtractor = new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethod)

    SwaggerOperationResponseClassReader sut =
        new SwaggerOperationResponseClassReader(
            new TypeResolver(),
            new JacksonEnumTypeDeterminer(),
            typeNameExtractor)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    if (operation.responseModel.collection) {
      assert expectedClass == String.format("%s[%s]", operation.responseModel.type, operation.responseModel.itemType)
    } else {
      assert expectedClass == operation.responseModel.type
    }

    if (allowableValues == null) {
      assert operation.responseModel.allowableValues == null
    } else {
      assert allowableValues == operation.responseModel.allowableValues.values
    }

    and:
    !sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)

    where:
    handlerMethod                                                        | expectedClass       | allowableValues
    dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'     | null
    dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'     | null
    dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»' | null
    dummyHandlerMethod('methodApiResponseClass')                         | 'FunkyBusiness'     | null
    dummyHandlerMethod('methodWithGenericPrimitiveArray')                | 'Array[byte]'       | null
    dummyHandlerMethod('methodWithGenericComplexArray')                  | 'Array[DummyClass]' | null
    dummyHandlerMethod('methodWithEnumResponse')                         | 'string'            | ['ONE', 'TWO']
  }

}
