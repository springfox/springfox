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
import spock.lang.Unroll
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.ModelRef
import springfox.documentation.schema.ModelReference
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.dummy.ResponseHeaderTestController
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.mixins.ServicePluginsSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class SwaggerResponseMessageReaderSpec extends DocumentationContextSpec {

  def "ApiResponse annotation should override when using swagger reader"() {
    given:
      OperationContext operationContext = new OperationContext(
          new OperationBuilder(
              new CachingOperationNameGenerator()),
              RequestMethod.GET,
              dummyHandlerMethod('methodWithApiResponses'),
              0,
              requestMappingInfo('/somePath'),
              context(),
              "")

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
      OperationContext operationContext = new OperationContext(
          new OperationBuilder(new CachingOperationNameGenerator()),
          RequestMethod.GET,
          dummyHandlerMethod('methodApiResponseClass'),
          0,
          requestMappingInfo('/somePath'),
          context(),
          "")

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

  @Unroll
  def "ApiOperation#responseHeaders and ApiResponse#responseHeader are merged for method #methodName"() {
    given:
      OperationContext operationContext = new OperationContext(
          new OperationBuilder(new CachingOperationNameGenerator()),
          RequestMethod.GET,
          handlerMethodIn(ResponseHeaderTestController, methodName),
          0,
          requestMappingInfo('/somePath'),
          context(),
          "")

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
      responseMessages.size() == 0 || responseMessages.inject(true) {
        soFar, r ->
          soFar &&
          r.headers.size() == headers.size() &&
          headersMatch(r.headers, headers)
      }

    where:
      methodName                | headers
      "noAnnnotationHeaders"    | []
      "defaultWithBoth"         | []
      "operationHeadersOnly"    | [["name": "header1", "type": new ModelRef("string")]]
      "operationHeadersOnly"    | [["name": "header1", "type": new ModelRef("List", new ModelRef("string"))]]
      "responseHeadersOnly"     | [["name": "header1", "type": new ModelRef("string")]]
      "bothWithOverride"        | [["name": "header1", "type": new ModelRef("int")]]
      "bothWithoutOverride"     | [["name": "header2", "type": new ModelRef("int")],["name": "header1", "type": new ModelRef("string")]]
  }

  boolean headersMatch(Map<String, ModelReference> headers, def expectedHeaders) {
    if (headers.size() == expectedHeaders.size()) {
      def retValue = true
      headers.eachWithIndex { Map.Entry<String, ModelReference> entry, int i ->
        if (entry.key != expectedHeaders.get(i).name ||
            entry.value.type != expectedHeaders.get(i).type.type ||
            entry.value.itemType != expectedHeaders.get(i).type.itemType) {
          retValue &= false
        }
      }
      return retValue
    }
    return false
  }

  def "Successful status series is inferred" () {
    expect:
      SwaggerResponseMessageReader.isSuccessful(status)
    where:
      status << [200, 204]
  }

  def "Unknown integers are treated as failures" () {
    expect:
      !SwaggerResponseMessageReader.isSuccessful(1001)
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
