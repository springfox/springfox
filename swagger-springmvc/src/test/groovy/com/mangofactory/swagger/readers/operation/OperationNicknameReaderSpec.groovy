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
      currentHttpMethod | handlerMethod                                             | expected
      GET               | dummyHandlerMethod('methodWithNickname')                  | 'myNickname'
      GET               | dummyHandlerMethod('methodWithHttpGETMethod')             | 'get_public_void_com_mangofactory_swagger_dummy_DummyClass_methodWithHttpGETMethod__'
      POST              | dummyHandlerMethod('methodWithHttpGETMethod')             | 'post_public_void_com_mangofactory_swagger_dummy_DummyClass_methodWithHttpGETMethod__'
      POST              | dummyHandlerMethod('methodWithGenericType')               | 'post_public_com_mangofactory_swagger_dummy_DummyModels_com_mangofactory_swagger_dummy_DummyModels_Paginated_com_mangofactory_swagger_dummy_DummyClass_BusinessType__com_mangofactory_swagger_dummy_DummyClass_methodWithGenericType__'
      POST              | dummyHandlerMethod('methodWithTwoParams', String, String) | 'post_public_void_com_mangofactory_swagger_dummy_DummyClass_methodWithTwoParams_java_lang_String_java_lang_String_'
  }
}
