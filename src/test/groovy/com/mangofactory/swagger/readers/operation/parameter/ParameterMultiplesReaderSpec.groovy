package com.mangofactory.swagger.readers.operation.parameter
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ParameterMultiplesReaderSpec extends Specification {

//   @Unroll
   def "param multiples"() {
    given:
      HandlerMethod handlerMethod = Stub()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterType() >> paramType
      context.put("methodParameter", methodParameter)

    when:
      Command operationCommand = new ParameterMultiplesReader();
      operationCommand.execute(context)
    then:
      context.get('allowMultiple') == expected
    where:
      apiParamAnnotation                       | paramType        | expected
      [allowMultiple: {-> true }] as ApiParam  | null             | true
      [allowMultiple: {-> false }] as ApiParam | String[].class   | false
      null                                     | String[].class   | true
      null                                     | List.class       | true
      null                                     | Collection.class | true
      null                                     | Set.class        | true
      null                                     | Vector.class     | true
      null                                     | Object[].class   | true
      null                                     | Integer.class    | false
      null                                     | Iterable.class   | true
   }
}
