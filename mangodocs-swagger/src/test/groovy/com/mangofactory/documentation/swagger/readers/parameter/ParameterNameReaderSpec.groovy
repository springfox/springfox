package com.mangofactory.documentation.swagger.readers.parameter
import com.google.common.base.Optional
import com.mangofactory.documentation.service.model.ResolvedMethodParameter
import com.mangofactory.documentation.builder.ParameterBuilder
import com.mangofactory.documentation.spi.service.contexts.ParameterContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.ModelProviderForServiceSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterNameReaderSpec extends DocumentationContextSpec {
//   @Unroll
  def "param required"() {
    given:
      ParameterAnnotationReader annotations = Mock(ParameterAnnotationReader)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      annotations.fromHierarchy(methodParameter, ApiParam.class) >> { return Optional.absent() }
      annotations.getParentInterfaces(methodParameter) >> []
      methodParameter.getParameterName() >> "default"
      methodParameter.getParameterAnnotations() >> [annotation]
      methodParameter.getParameterIndex() >> 0
      ResolvedMethodParameter resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())
    when:
      def operationCommand = new com.mangofactory.documentation.spring.web.readers.parameter.ParameterNameReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().name == expected
    where:
      annotation                                       | expected
      [value: { -> "pathV" }] as PathVariable          | "pathV"
      [value: { -> "pathModelAtt" }] as ModelAttribute | "pathModelAtt"
      [value: { -> "reqHeaderAtt" }] as RequestHeader  | "reqHeaderAtt"
      [value: { -> "RequestParam" }] as RequestParam   | "RequestParam"
      null                                             | "default"
  }
}
