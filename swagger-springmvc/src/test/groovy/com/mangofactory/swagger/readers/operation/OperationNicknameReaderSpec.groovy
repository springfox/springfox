package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST

@Mixin(RequestMappingSupport)
class OperationNicknameReaderSpec extends Specification {

  @Unroll
  def "should determine nickname"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      OperationNicknameReader reader = new OperationNicknameReader()
      context.put("currentHttpMethod", currentHttpMethod)
    when:
      reader.execute(context)
      String nickname = context.getResult().nickname

    then:
      nickname == expected
    where:
      currentHttpMethod | handlerMethod                                 | expected
      GET               | dummyHandlerMethod('methodWithNickname')      | 'myNickname'
      GET               | dummyHandlerMethod('methodWithHttpGETMethod') | 'get-public void com.mangofactory.swagger.dummy.DummyClass.methodWithHttpGETMethod()'
      POST              | dummyHandlerMethod('methodWithHttpGETMethod') | 'post-public void com.mangofactory.swagger.dummy.DummyClass.methodWithHttpGETMethod()'
  }
}
