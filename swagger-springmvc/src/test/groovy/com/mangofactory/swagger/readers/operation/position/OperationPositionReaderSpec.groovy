package com.mangofactory.swagger.readers.operation.position
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.operation.position.OperationPositionReader
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class OperationPositionReaderSpec extends Specification {

   def "should have correct api position after several invocations"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      OperationPositionReader operationPositionReader = new OperationPositionReader();
      context.put("currentCount", 0)
    when:
      numCalls.times { operationPositionReader.execute(context) }
      Map<String, Object> result = context.getResult()

    then:
      result['currentCount'] == expectedCurrentCount
      result['position'] == expectedLastPosition
    where:
      numCalls | handlerMethod                            | expectedCurrentCount | expectedLastPosition
      1        | dummyHandlerMethod()                     | 1                    | 0
      2        | dummyHandlerMethod()                     | 2                    | 1
      5        | dummyHandlerMethod()                     | 5                    | 4
      2        | dummyHandlerMethod('methodWithPosition') | 6                    | 5
   }
}