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

package springfox.documentation.swagger.readers.operation

import com.fasterxml.classmate.TypeResolver
import org.springframework.mock.env.MockEnvironment
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.property.bean.AccessorsProvider
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.DescriptionResolver
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.OperationParameterReader
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class OperationImplicitParamsReaderSpec extends DocumentationContextSpec {

  def "Should add implicit parameters"() {
    given:
    OperationContext operationContext = operationContext(documentationContext(), handlerMethod, 0)
    def resolver = new TypeResolver()
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    def plugins = defaultWebPlugins()
    def expander = new ModelAttributeParameterExpander(
        new FieldProvider(resolver),
        new AccessorsProvider(resolver),
        enumTypeDeterminer)
    expander.pluginsManager = plugins
    OperationParameterReader sut = new OperationParameterReader(expander, enumTypeDeterminer)
    sut.pluginsManager = plugins
    def env = new DescriptionResolver(new MockEnvironment())
    OperationImplicitParametersReader operationImplicitParametersReader = new OperationImplicitParametersReader(env)
    OperationImplicitParameterReader operationImplicitParameterReader = new OperationImplicitParameterReader(env)

    when:
    sut.apply(operationContext)
    operationImplicitParametersReader.apply(operationContext)
    operationImplicitParameterReader.apply(operationContext)

    and:
    def operation = operationContext.operationBuilder().build()

    then:
    operation.parameters.size() == expectedSize

    and:
    !operationImplicitParametersReader.supports(DocumentationType.SPRING_WEB)
    operationImplicitParametersReader.supports(DocumentationType.SWAGGER_12)
    operationImplicitParametersReader.supports(DocumentationType.SWAGGER_2)

    and:
    !operationImplicitParameterReader.supports(DocumentationType.SPRING_WEB)
    operationImplicitParameterReader.supports(DocumentationType.SWAGGER_12)
    operationImplicitParameterReader.supports(DocumentationType.SWAGGER_2)
    where:
    handlerMethod                                                                   | expectedSize
    dummyHandlerMethod('dummyMethod')                                               | 0
    dummyHandlerMethod('methodWithApiImplicitParam')                                | 1
    dummyHandlerMethod('methodWithApiImplicitParamAndInteger', Integer.class)       | 2
    dummyHandlerMethod('methodWithApiImplicitParamAndExample', Integer.class)       | 2
    dummyHandlerMethod('methodWithApiImplicitParamAndAllowMultiple', Integer.class) | 2
    dummyHandlerMethod('methodWithApiImplicitParams', Integer.class)                | 3
    handlerMethodIn(apiImplicitParamsClass(), 'methodWithApiImplicitParam')         | 2
    dummyHandlerMethodIn(apiImplicitParamsAllowMultipleClass(), 'methodWithApiImplicitParam')   | 3
  }
}
