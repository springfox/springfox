package springdox.documentation.spring.web.readers.operation

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMethod
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.schema.DefaultGenericTypeNamingStrategy
import springdox.documentation.schema.TypeNameExtractor
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.service.ResponseMessage
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

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
    and:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
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
      responseMessage.getResponseModel().type == 'BusinessModel'
      responseMessage.getMessage() == "OK"
  }

  def "Methods with return type containing a container model should override the success response code"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithListOfBusinesses'), 0, requestMappingInfo('/somePath'),
              context(), "")
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
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithResponseStatusAnnotation'), 0,
              requestMappingInfo('/somePath'), context(), "")
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
}
