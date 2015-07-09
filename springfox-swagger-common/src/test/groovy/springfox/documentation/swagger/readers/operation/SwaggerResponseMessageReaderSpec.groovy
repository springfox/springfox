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
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class SwaggerResponseMessageReaderSpec extends DocumentationContextSpec {

  def "ApiResponse annotation should override when using swagger reader"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithApiResponses'), 0, requestMappingInfo('/somePath'),
              context(), "")

      PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

      def resolver = new TypeResolver()
      def typeNameExtractor = new TypeNameExtractor(resolver,  modelNameRegistry)

    when:
      new SwaggerResponseMessageReader(typeNameExtractor, resolver).apply(operationContext)

    and:
      def operation = operationContext.operationBuilder().build()
      def responseMessages = operation.responseMessages

    then:
      responseMessages.size() == 1
      def annotatedResponse = responseMessages.find { it.code == 413 }
      annotatedResponse != null
      annotatedResponse.message == "a message"
  }

  def "ApiOperation annotation should provide response"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
          RequestMethod.GET, dummyHandlerMethod('methodApiResponseClass'), 0, requestMappingInfo('/somePath'),
          context(), "")

      PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
          OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

      def resolver = new TypeResolver()
      def typeNameExtractor = new TypeNameExtractor(resolver,  modelNameRegistry)

    when:
      new SwaggerResponseMessageReader(typeNameExtractor, resolver).apply(operationContext)

    and:
      def operation = operationContext.operationBuilder().build()
      def responseMessages = operation.responseMessages

    then:
      responseMessages.size() == 1
      def annotatedResponse = responseMessages.find { it.code == 200 }
      annotatedResponse != null
      annotatedResponse.message == "OK"
  }

  def "Supports all documentation types"() {
    given:
      PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

      def resolver = new TypeResolver()
      def typeNameExtractor = new TypeNameExtractor(resolver,  modelNameRegistry)

    when:
      def sut = new SwaggerResponseMessageReader(typeNameExtractor, resolver)

    then:
      !sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }
}
