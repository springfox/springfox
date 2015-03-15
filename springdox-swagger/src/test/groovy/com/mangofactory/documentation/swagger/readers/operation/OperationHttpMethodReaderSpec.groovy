package com.mangofactory.documentation.swagger.readers.operation
import com.mangofactory.documentation.builders.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport])
class OperationHttpMethodReaderSpec extends DocumentationContextSpec {
  def "should return api method annotation when present"() {

    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              currentHttpMethod, handlerMethod, 0, requestMappingInfo("/somePath"),
              context(), "/anyPath")

      OperationHttpMethodReader operationMethodReader = new OperationHttpMethodReader();
    when:
      operationMethodReader.apply(operationContext)
    and:
      def operation = operationContext.operationBuilder().build()

    then:
      operation.method == expected
    where:
      currentHttpMethod  | handlerMethod                                     | expected
      RequestMethod.GET  | dummyHandlerMethod()                              | null
      RequestMethod.PUT  | dummyHandlerMethod()                              | null
      RequestMethod.POST | dummyHandlerMethod('methodWithHttpGETMethod')     | 'GET'
      RequestMethod.POST | dummyHandlerMethod('methodWithInvalidHttpMethod') | null
  }
}
