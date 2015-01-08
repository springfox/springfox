package com.mangofactory.swagger.readers.operation

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.service.model.ResponseMessage
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.springmvc.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.SwaggerResponseMessageReader
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport])
class DefaultResponseMessageReaderSpec extends DocumentationContextSpec {
  ResponseMessagesReader sut

  def setup() {
    sut = new ResponseMessagesReader(defaultValues.typeResolver, defaultValues.alternateTypeProvider)
  }
  def "Should add default response messages"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              currentHttpMethod, handlerMethod, 0, requestMappingInfo('/somePath'),
              context(), "")
    when:
      sut.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()
      def responseMessages = operation.responseMessages

    then:
      def allResponses = responseMessages.collect { it.code }
      assert ecpectedCodes.size() == allResponses.intersect(ecpectedCodes).size()
    where:
      currentHttpMethod | handlerMethod        | ecpectedCodes
      RequestMethod.GET | dummyHandlerMethod() | [200, 404, 403, 401]
  }

  def "swagger annotation should override when using default reader"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithApiResponses'), 0, requestMappingInfo('/somePath'),
              context(), "")
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

  def "swagger annotation should override when using swagger reader"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithApiResponses'), 0, requestMappingInfo('/somePath'),
              context(), "")
    when:
      new SwaggerResponseMessageReader(new TypeResolver()).apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()
      def responseMessages = operation.responseMessages

    then:
      responseMessages.size() == 1
      def annotatedResponse = responseMessages.find { it.code == 413 }
      annotatedResponse != null
      annotatedResponse.message == "a message"
  }

  def "Methods with return type containing a model should override the success response code"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithConcreteResponseBody'), 0, requestMappingInfo('/somePath'),
              context(), "")
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()
      def responseMessages = operation.responseMessages
    then:
      ResponseMessage responseMessage = responseMessages.find { it.code == 200 }
      responseMessage.getCode() == 200
      responseMessage.getResponseModel() == 'BusinessModel'
      responseMessage.getMessage() == "OK"
  }
}
