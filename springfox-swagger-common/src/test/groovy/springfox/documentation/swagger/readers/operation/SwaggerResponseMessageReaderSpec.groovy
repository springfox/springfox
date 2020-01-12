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
import springfox.documentation.schema.*
import springfox.documentation.service.Header
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.EnumTypeDeterminer
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.dummy.ResponseExampleTestController
import springfox.documentation.spring.web.dummy.ResponseHeaderTestController
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class SwaggerResponseMessageReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {

  def "ApiResponse annotation should override when using swagger reader"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithApiResponses'))

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

    def resolver = new TypeResolver()
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    when:
    new SwaggerResponseMessageReader(
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor,
        resolver)
      .apply(operationContext)

    and:
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages

    then:
    responseMessages.size() == 2
    def annotatedResponse = responseMessages.find { it.code == 413 }
    annotatedResponse != null
    annotatedResponse.message == "a message"
    def classLevelResponse = responseMessages.find { it.code == 404 }
    classLevelResponse != null
    classLevelResponse.message == "Not Found"
  }

  def "ApiOperation annotation should provide response"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodApiResponseClass'))

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

    def resolver = new TypeResolver()
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    when:
    new SwaggerResponseMessageReader(
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor,
        resolver)
      .apply(operationContext)

    and:
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages

    then:
    responseMessages.size() == 2
    def annotatedResponse = responseMessages.find { it.code == 200 }
    annotatedResponse != null
    annotatedResponse.message == "OK"
    def classLevelResponse = responseMessages.find { it.code == 404 }
    classLevelResponse != null
    classLevelResponse.message == "Not Found"
  }

  @Unroll
  def "ApiOperation#responseHeaders and ApiResponse#responseHeader are merged for method #methodName"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethodIn(ResponseHeaderTestController, methodName))

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

    def resolver = new TypeResolver()
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    when:
    new SwaggerResponseMessageReader(new JacksonEnumTypeDeterminer(),
        typeNameExtractor,
        resolver)
      .apply(operationContext)

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
    methodName             | headers
    "noAnnotationHeaders"  | []
    "defaultWithBoth"      | []
    "operationHeadersOnly" | [["name": "header1", "type": new ModelRef("string")]]
    "operationHeadersOnly" | [["name": "header1", "type": new ModelRef("List", new ModelRef("string"))]]
    "responseHeadersOnly"  | [["name": "header1", "type": new ModelRef("string")]]
    "bothWithOverride"     | [["name": "header1", "type": new ModelRef("int")]]
    "bothWithoutOverride"  | [["name": "header1", "type": new ModelRef("string")], ["name": "header2", "type": new ModelRef("int")]]
  }

  boolean headersMatch(Map<String, ModelReference> headers, List<Header> expectedHeaders) {
    if (headers.size() == expectedHeaders.size()) {
      def retValue = true
      headers.eachWithIndex { Map.Entry<String, ModelReference> entry, int i ->
        if (entry.key != expectedHeaders.get(i).name ||
            entry.value.modelReference.type != expectedHeaders.get(i).type.type ||
            entry.value.modelReference.itemType != expectedHeaders.get(i).type.itemType) {
          retValue &= false
        }
      }
      return retValue
    }
    return false
  }

  def "Successful status series is inferred"() {
    expect:
    SwaggerResponseMessageReader.isSuccessful(status)
    where:
    status << [200, 204]
  }

  def "Unknown integers are treated as failures"() {
    expect:
    !SwaggerResponseMessageReader.isSuccessful(1001)
  }

  def "Supports all documentation types"() {
    given:
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

    def resolver = new TypeResolver()
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    when:
    def sut = new SwaggerResponseMessageReader(
        new JacksonEnumTypeDeterminer(),
        typeNameExtractor,
        resolver)

    then:
    !sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    sut.supports(DocumentationType.SWAGGER_2)
  }

  @Unroll
  def "Supports examples"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), handlerMethodIn(ResponseExampleTestController, methodName))

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])

    def resolver = new TypeResolver()
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    when:
    new SwaggerResponseMessageReader(
        Mock(EnumTypeDeterminer),
        typeNameExtractor,
        resolver)
        .apply(operationContext)

    and:
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages

    then:
    examplesMatch(responseMessages[0].examples, examples)

    where:
    methodName                  | examples
    "operationWithNoExamples"   | []
    "operationWithOneExample"   | [new Example("mediaType", "value")]
    "operationWithTwoExamples"  | [new Example("mediaType1", "value1"), new Example("mediaType2", "value2")]
    "operationWithEmptyExample" | [new Example("mediaType1", "value1")]
  }

  boolean examplesMatch(List<Example> examples, List<Example> expectedExamples) {
    if (examples.size() != expectedExamples.size()) {
      return false
    }
    for (def i = 0; i < examples.size(); i++) {
      if (examples[i].mediaType != expectedExamples[i].mediaType
          || examples[i].value != expectedExamples[i].value) {
        return false
      }
    }
    return true
  }
}
