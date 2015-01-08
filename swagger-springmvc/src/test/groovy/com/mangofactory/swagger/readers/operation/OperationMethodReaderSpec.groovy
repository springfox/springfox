package com.mangofactory.swagger.readers.operation
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.springmvc.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.OperationHttpMethodReader
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport])
class OperationMethodReaderSpec extends DocumentationContextSpec {
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
