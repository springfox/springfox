package com.mangofactory.documentation.spring.web.readers.operation
import com.mangofactory.documentation.builders.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport])
class OperationMethodReaderSpec extends DocumentationContextSpec {


  def "should return api method when using default reader"() {

    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              currentHttpMethod, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      def operationMethodReader = new DefaultOperationBuilder();
    when:
      operationMethodReader.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()

    then:
      operation.method == expected
    where:
      currentHttpMethod  | handlerMethod                                     | expected
      RequestMethod.GET  | dummyHandlerMethod()                              | "GET"
      RequestMethod.PUT  | dummyHandlerMethod()                              | "PUT"
      RequestMethod.POST | dummyHandlerMethod('methodWithHttpGETMethod')     | 'POST'
      RequestMethod.POST | dummyHandlerMethod('methodWithInvalidHttpMethod') | "POST"
  }

}
