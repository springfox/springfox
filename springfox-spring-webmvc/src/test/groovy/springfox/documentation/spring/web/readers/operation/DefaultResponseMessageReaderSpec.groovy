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
import org.springframework.http.HttpStatus
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.Model
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.property.ModelSpecificationFactory
import springfox.documentation.service.ResponseMessage
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

class DefaultResponseMessageReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  ResponseMessagesReader sut

  def setup() {
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.of([new DefaultTypeNameProvider()])
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    def typeNameExtractor = new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        enumTypeDeterminer)
    sut = new ResponseMessagesReader(
        enumTypeDeterminer,
        typeNameExtractor,
        defaultSchemaPlugins(),
        new ModelSpecificationFactory(typeNameExtractor, enumTypeDeterminer),
        defaultWebPlugins())
  }

  def "Should add default response messages"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(),
            handlerMethod,
            0,
            requestMappingInfo("/somePath"),
            currentHttpMethod)
    when:
    sut.apply(operationContext)
    and:
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages

    then:
    def allResponses = responseMessages.collect { it.code }
    assert ecpectedCodes.size() == allResponses.intersect(ecpectedCodes).size()
    and:
    sut.supports(DocumentationType.SPRING_WEB)
    sut.supports(DocumentationType.SWAGGER_12)
    where:
    currentHttpMethod | handlerMethod        | ecpectedCodes
    RequestMethod.GET | dummyHandlerMethod() | [200, 404, 403, 401]
  }

  def "swagger annotation should override when using default reader"() {
    given:
    OperationContext operationContext =
        operationContext(
            documentationContext(),
            dummyHandlerMethod('methodWithApiResponses'))

    when:
    sut.apply(operationContext)
    and:
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages

    then:
    responseMessages.size() == 4
    def annotatedResponse = responseMessages.find { it.code == 413 }
    annotatedResponse == null
  }


  def "Methods with return type containing a model should override the success response code"() {
    given:
    def knownModels = new HashMap<String, List<Model>>()
    knownModels.put("0_0", new HashSet<Model>())
    OperationContext operationContext =
        operationContext(documentationContext(),
            dummyHandlerMethod('methodWithConcreteResponseBody'),
            0,
            requestMappingInfo("/somePath"),
            RequestMethod.GET,
            knownModels)

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages
    then:
    ResponseMessage responseMessage = responseMessages.find { it.code == 200 }
    responseMessage.getCode() == 200
    responseMessage.getResponseModel().type == 'BusinessModel'
    responseMessage.getMessage() == "OK"
  }

  def "Methods with return type containing a container model should override the success response code"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithListOfBusinesses'))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages
    then:
    ResponseMessage responseMessage = responseMessages.find { it.code == 200 }
    responseMessage.getCode() == 200
    responseMessage.getResponseModel().type == 'List'
    responseMessage.getResponseModel().itemType == 'BusinessModel'
    responseMessage.getMessage() == "OK"
  }

  def "Methods with return type containing ResponseStatus annotation"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithResponseStatusAnnotation'))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages
    then:
    ResponseMessage responseMessage = responseMessages.find { it.code == 202 }
    responseMessage.getCode() == HttpStatus.ACCEPTED.value()
    responseMessage.getResponseModel().type == 'BusinessModel'
    responseMessage.getMessage() == "Accepted request"
  }

  def "Methods with return type containing ResponseStatus annotation and empty reason message"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethod('methodWithResponseStatusAnnotationAndEmptyReason'))

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()
    def responseMessages = operation.responseMessages
    then:
    ResponseMessage responseMessage = responseMessages.find { it.code == 204 }
    responseMessage.getCode() == HttpStatus.NO_CONTENT.value()
    responseMessage.getMessage() == HttpStatus.NO_CONTENT.reasonPhrase
  }
}
