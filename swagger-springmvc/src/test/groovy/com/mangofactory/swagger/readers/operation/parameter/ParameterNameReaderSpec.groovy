package com.mangofactory.swagger.readers.operation.parameter

import com.google.common.base.Optional
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ParameterNameReaderSpec extends Specification {

//   @Unroll
  def "param required"() {
    given:
      HandlerMethod handlerMethod = Mock(HandlerMethod)
      ParameterAnnotationReader annotations = Mock(ParameterAnnotationReader)
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      annotations.fromHierarchy(methodParameter, ApiParam.class) >> { return Optional.absent() }
      annotations.getParentInterfaces(methodParameter) >> []
      methodParameter.getParameterName() >> "default"
      methodParameter.getParameterAnnotations() >> [annotation]
      methodParameter.getParameterIndex() >> 0
      context.put("methodParameter", methodParameter)

    when:
      Command operationCommand = new ParameterNameReader(annotations);
      operationCommand.execute(context)
    then:
      context.get('name') == expected
    where:
      annotation                                       | expected
      [value: { -> "pathV" }] as PathVariable          | "pathV"
      [value: { -> "pathModelAtt" }] as ModelAttribute | "pathModelAtt"
      [value: { -> "reqHeaderAtt" }] as RequestHeader  | "reqHeaderAtt"
      [value: { -> "RequestParam" }] as RequestParam   | "RequestParam"
      null                                             | "default"
  }
}
