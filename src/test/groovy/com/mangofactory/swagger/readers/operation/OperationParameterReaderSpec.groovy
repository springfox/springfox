package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.validation.BindingResult
import spock.lang.Ignore
import spock.lang.Specification

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Mixin(RequestMappingSupport)
class OperationParameterReaderSpec extends Specification {

   @Ignore
   def "Should ignore ignorables"() {
    given:
      List ignorableParameterTypes = [ServletRequest, ServletResponse, HttpServletRequest, HttpServletResponse, BindingResult, ServletContext]
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      context.put("ignorableParameterTypes", ignorableParameterTypes as Set)
    when:
      OperationParameterReader operationParameterReader = new OperationParameterReader()
      operationParameterReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['parameters'].size == expectedSize

    where:
      handlerMethod                                                        | expectedSize
      dummyHandlerMethod('methodWithServletRequest', ServletRequest.class) | 0
      dummyHandlerMethod('methodWithBindingResult', BindingResult.class)   | 0
      dummyHandlerMethod('methodWithInteger', Integer.class)               | 1


   }
}
