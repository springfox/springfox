package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class OperationSummaryReaderSpec extends Specification {

   def "should set summary based on method name or swagger annotation"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      OperationSummaryReader operationSummaryReader = new OperationSummaryReader();
    when:
      operationSummaryReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['summary'] == expected
    where:
      handlerMethod                           | expected
      dummyHandlerMethod()                    | 'dummyMethod'
      dummyHandlerMethod('methodWithSummary') | 'summary'
   }
}
