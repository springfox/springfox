package springdox.documentation.swagger.readers.operation

import org.springframework.web.bind.annotation.RequestMethod
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.spi.service.contexts.OperationContext
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.mixins.ServicePluginsSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ServicePluginsSupport])
class SwaggerResponseMessageReaderSpec extends DocumentationContextSpec {

  def "swagger annotation should override when using swagger reader"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithApiResponses'), 0, requestMappingInfo('/somePath'),
              context(), "")
    when:
      new SwaggerResponseMessageReader().apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()
      def responseMessages = operation.responseMessages

    then:
      responseMessages.size() == 1
      def annotatedResponse = responseMessages.find { it.code == 413 }
      annotatedResponse != null
      annotatedResponse.message == "a message"
  }
}
