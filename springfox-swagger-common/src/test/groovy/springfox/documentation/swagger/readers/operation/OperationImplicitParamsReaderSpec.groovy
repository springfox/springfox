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

package springfox.documentation.swagger.readers.operation
import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.spring.web.readers.operation.OperationParameterReader
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class OperationImplicitParamsReaderSpec extends DocumentationContextSpec {

  def "Should add implicit parameters"() {
    given:

      OperationContext operationContext = new OperationContext(
          new OperationBuilder(new CachingOperationNameGenerator()),
          RequestMethod.GET,
          new RequestMappingContext(context(),
              new WebMvcRequestHandler(
                  requestMappingInfo("/somePath"),
                  handlerMethod)), 0)

      def resolver = new TypeResolver()

      def plugins = defaultWebPlugins()
      def expander = new ModelAttributeParameterExpander(new FieldProvider(resolver))
      expander.pluginsManager = plugins
      OperationParameterReader sut = new OperationParameterReader(resolver, expander)
      sut.pluginsManager = plugins
      OperationImplicitParametersReader operationImplicitParametersReader = new OperationImplicitParametersReader()
      OperationImplicitParameterReader operationImplicitParameterReader = new OperationImplicitParameterReader()
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
      handlerMethod                                                             | expectedSize
      dummyHandlerMethod('dummyMethod')                                         | 0
      dummyHandlerMethod('methodWithApiImplicitParam')                          | 1
      dummyHandlerMethod('methodWithApiImplicitParamAndInteger', Integer.class) | 2
      dummyHandlerMethod('methodWithApiImplicitParams', Integer.class)          | 3
      handlerMethodIn(apiImplicitParamsClass(), 'methodWithApiImplicitParam')   | 2
  }
}
