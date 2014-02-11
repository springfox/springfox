package com.mangofactory.swagger.readers.operation.parameter
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ParameterRequiredReaderSpec extends Specification {

//   @Unroll
   def "param required"() {
    given:
      HandlerMethod handlerMethod = Stub()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation, requestParamAnnotation ]
      context.put("methodParameter", methodParameter)

    when:
      Command operationCommand = new ParameterRequiredReader();
      operationCommand.execute(context)
    then:
      context.get('required') == expected
    where:
      apiParamAnnotation                  | requestParamAnnotation                  | expected
      [required: {-> true }] as ApiParam  | null                                    | true
      [required: {-> false }] as ApiParam | null                                    | false
      null                                | [required: {-> true }] as RequestParam  | true
      null                                | [required: {-> false }] as RequestParam | false
      [required: {-> true }] as ApiParam  | [required: {-> false }] as RequestParam | true
      [required: {-> false }] as ApiParam | [required: {-> true }] as RequestParam  | false
   }

//   @Unroll
   def "header"() {
      HandlerMethod handlerMethod = Stub()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      methodParameter.getParameterAnnotations() >> [requestParamAnnotation]
      context.put("methodParameter", methodParameter)
    when:
      Command operationCommand = new ParameterRequiredReader();
      operationCommand.execute(context)
    then:
      context.get('required') == expected
    where:
      requestParamAnnotation                  | expected
      null                                    | true
      [required: {-> true }] as RequestHeader | true
      [required: {-> false }] as RequestHeader| false
   }
}
