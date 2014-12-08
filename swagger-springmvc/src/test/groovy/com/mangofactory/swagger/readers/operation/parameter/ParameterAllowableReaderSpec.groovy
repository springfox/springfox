package com.mangofactory.swagger.readers.operation.parameter

import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import com.mangofactory.swagger.models.dto.AllowableListValues
import com.mangofactory.swagger.models.dto.AllowableRangeValues
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification
import spock.lang.Unroll

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

      allowableValues.getValueType() == "LIST"
      allowableValues.getValues() == ["PRODUCT", "SERVICE"]
    where:
      handler                                                                          | expected
      dummyHandlerMethod('methodWithSingleEnum', DummyClass.BusinessType.class)        | AllowableListValues
      dummyHandlerMethod('methodWithSingleEnumArray', DummyClass.BusinessType[].class) | AllowableListValues
  }

  @Unroll
  def "Api annotation with list type"() {
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
      allowableValues.getValueType() == "LIST"
      allowableValues.getValues() == expected
    where:
      apiParamAnnotation                                | expected
      [allowableValues: { -> "1, 2" }] as ApiParam      | ['1', '2']
      [allowableValues: { -> "1,2,3,4" }] as ApiParam   | ['1', '2', '3', '4']
      [allowableValues: { -> "1,2,   ,4" }] as ApiParam | ['1', '2', '4']
      [allowableValues: { -> "1" }] as ApiParam         | ['1']
  }

  @Unroll("Range: #min | #max")
  def "Api annotation with ranges"() {
    given:
      HandlerMethod handlerMethod = Stub()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation]
      context.put("methodParameter", methodParameter)

    when:
      Command operationCommand = new ParameterAllowableReader();
      operationCommand.execute(context)
      AllowableRangeValues allowableValues = context.get('allowableValues')
    then:
      allowableValues.getMin() == min as String
      allowableValues.getMax() == max as String
    where:
      apiParamAnnotation                                                         | min | max
      [allowableValues: { -> "range[1,5]" }] as ApiParam                         | 1   | 5
      [allowableValues: { -> "range[1,1]" }] as ApiParam                         | 1   | 1
      [allowableValues: { -> "range[2," + Integer.MAX_VALUE + "]" }] as ApiParam | 2   | Integer.MAX_VALUE
  }
}
