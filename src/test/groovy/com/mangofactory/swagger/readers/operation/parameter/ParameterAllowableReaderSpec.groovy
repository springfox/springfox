package com.mangofactory.swagger.readers.operation.parameter

import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import com.wordnik.swagger.model.AllowableListValues
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.fromScalaList

@Mixin(RequestMappingSupport)
class ParameterAllowableReaderSpec extends Specification {

   def "enum types"() {
    given:
      HandlerMethod handlerMethod = handler
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 0)
      context.put("methodParameter", methodParameter)

    when:
      Command operationCommand = new ParameterAllowableReader();
      operationCommand.execute(context)
      AllowableListValues allowableValues = context.get('allowableValues')
    then:
      allowableValues.valueType() == "LIST"
      fromScalaList(allowableValues.values()) == ["PRODUCT", "SERVICE"]
    where:
      handler                                                                   | expected
      dummyHandlerMethod('methodWithSingleEnum', DummyClass.BusinessType.class) | AllowableListValues
   }

   def "Api annotation"(){
      given:
      HandlerMethod handlerMethod = Stub()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation]
      context.put("methodParameter", methodParameter)

    when:
      Command operationCommand = new ParameterAllowableReader();
      operationCommand.execute(context)
      AllowableListValues allowableValues = context.get('allowableValues')
    then:
      allowableValues.valueType() == "LIST"
      fromScalaList(allowableValues.values()) == expected
    where:
      apiParamAnnotation                       | expected
      [allowableValues: {-> "1, 2" }] as ApiParam  | ['1','2']
      [allowableValues: {-> "1,2,3,4" }] as ApiParam | ['1', '2', '3', '4']
      [allowableValues: {-> "1,2,   ,4" }] as ApiParam | ['1', '2', '4']
   }
}
