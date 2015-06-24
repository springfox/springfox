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

package springfox.documentation.swagger1.readers.operation
import com.fasterxml.classmate.TypeResolver
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander
import springfox.documentation.spring.web.readers.operation.OperationParameterReader
import springfox.documentation.swagger.readers.operation.OperationImplicitParameterReader
import springfox.documentation.swagger.readers.operation.OperationImplicitParametersReader

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class OperationImplicitParamsReaderSpec extends DocumentationContextSpec {

  def "Should add implicit parameters"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")


      def resolver = new TypeResolver()

      def plugins = defaultWebPlugins()
      def expander = new ModelAttributeParameterExpander(resolver)
      expander.pluginsManager = plugins
      OperationParameterReader operationParameterReader = new OperationParameterReader(resolver,
        expander)
      operationParameterReader.pluginsManager = plugins
      OperationImplicitParametersReader operationImplicitParametersReader = new OperationImplicitParametersReader()
      OperationImplicitParameterReader operationImplicitParameterReader = new OperationImplicitParameterReader()
    when:
      operationParameterReader.apply(operationContext)
      operationImplicitParametersReader.apply(operationContext)
      operationImplicitParameterReader.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()
    then:
      operation.parameters.size() == expectedSize

    where:
      handlerMethod                                                             | expectedSize
      dummyHandlerMethod('dummyMethod')                                         | 0
      dummyHandlerMethod('methodWithApiImplicitParam')                          | 1
      dummyHandlerMethod('methodWithApiImplicitParamAndInteger', Integer.class) | 2
      dummyHandlerMethod('methodWithApiImplicitParams', Integer.class)          | 3
      handlerMethodIn(apiImplicitParamsClass(), 'methodWithApiImplicitParam')   | 2
  }
}
