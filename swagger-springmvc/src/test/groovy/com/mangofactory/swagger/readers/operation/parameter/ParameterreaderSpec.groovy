package com.mangofactory.swagger.readers.operation.parameter
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.readers.operation.ParameterAccessReader
import com.mangofactory.swagger.readers.operation.ParameterDefaultReader
import com.mangofactory.swagger.readers.operation.ParameterDescriptionReader
import com.mangofactory.swagger.readers.operation.ParameterNameReader
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ParameterReaderSpec extends Specification {
//   @Unroll("property #resultProperty expected: #expected")
   def "should set basic properties based on ApiParam annotation or a sensible default"() {
    given:
      HandlerMethod handlerMethod = Stub()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterAnnotation(RequestParam.class) >> reqParamAnnot
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation, reqParamAnnot]
      methodParameter."$springParameterMethod"() >> methodReturnValue

      context.put("methodParameter", methodParameter);
    when:
      Command operationCommand = command
      operationCommand.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result[resultProperty] == expected
    where:
      command                          | resultProperty | springParameterMethod | methodReturnValue | apiParamAnnotation                     | reqParamAnnot                          | expected
      new ParameterNameReader()        | 'name'         | 'getParameterName'    | 'someName'        | null                                   | null                                   | 'someName'
      new ParameterNameReader()        | 'name'         | 'none'                | 'any'             | apiParam([name: {-> 'AnName' }])       | null                                   | 'AnName'
      new ParameterNameReader()        | 'name'         | 'none'                | 'any'             | null                                   | reqParam([value: {-> 'ArName' }])      | 'ArName'
      new ParameterDescriptionReader() | 'description'  | 'getParameterName'    | 'someName'        | null                                   | null                                   | 'someName'
      new ParameterDescriptionReader() | 'description'  | 'none'                | 'any'             | apiParam([value: {-> 'AnDesc' }])      | null                                   | 'AnDesc'
      new ParameterDefaultReader()     | 'defaultValue' | 'none'                | 'any'             | null                                   | null                                   | ''
      new ParameterDefaultReader()     | 'defaultValue' | 'none'                | 'any'             | apiParam([defaultValue: {-> 'defl' }]) | null                                   | 'defl'
      new ParameterDefaultReader()     | 'defaultValue' | 'none'                | 'any'             | null                                   | reqParam([defaultValue: {-> 'defr' }]) | 'defr'
      new ParameterAccessReader()      | 'paramAccess'  | 'none'                | 'any'             | apiParam([access: {-> 'myAccess' }])   | null                                   | 'myAccess'
   }

   private ApiParam apiParam(Map closureMap) {
      closureMap as ApiParam
   }

   private RequestParam reqParam(Map closureMap) {
      closureMap as RequestParam
   }
}
