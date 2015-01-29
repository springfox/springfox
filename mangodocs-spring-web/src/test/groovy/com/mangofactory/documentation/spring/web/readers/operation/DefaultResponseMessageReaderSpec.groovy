package com.mangofactory.documentation.spring.web.readers.operation
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.service.model.ResponseMessage
import com.mangofactory.documentation.service.model.builder.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.ServicePluginsSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport, ServicePluginsSupport, SchemaPluginsSupport])
class DefaultResponseMessageReaderSpec extends DocumentationContextSpec {
  ResponseMessagesReader sut

  def setup() {
    def typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(),  defaultSchemaPlugins())
    sut = new ResponseMessagesReader(new TypeResolver(), typeNameExtractor)
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
