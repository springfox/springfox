package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class OperationMethodReaderSpec extends Specification {

   def "should return api method annotation when present"() {

    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)

      context.put("currentHttpMethod", currentHttpMethod)
      OperationHttpMethodReader operationMethodReader = new OperationHttpMethodReader();
    when:
      operationMethodReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['httpRequestMethod'] == expected
    where:
      currentHttpMethod  | handlerMethod                                     | expected
      RequestMethod.GET  | dummyHandlerMethod()                              | 'GET'
      RequestMethod.PUT  | dummyHandlerMethod()                              | 'PUT'
      RequestMethod.POST | dummyHandlerMethod('methodWithHttpGETMethod')     | 'GET'
      RequestMethod.POST | dummyHandlerMethod('methodWithInvalidHttpMethod') | 'POST'
   }

}
