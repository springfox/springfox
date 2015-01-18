package com.mangofactory.documentation.swagger.readers.parameter

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.service.model.ResolvedMethodParameter
import com.mangofactory.documentation.service.model.builder.ParameterBuilder
import com.mangofactory.documentation.spi.service.contexts.ParameterContext
import com.mangofactory.documentation.spring.web.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.dummy.DummyClass
import com.mangofactory.documentation.spring.web.mixins.ModelProviderForServiceSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterMultiplesReaderSpec extends DocumentationContextSpec {
  def "param multiples for swagger reader"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterType() >> paramType
      ResolvedType resolvedType = paramType != null ? new TypeResolver().resolve(paramType) : null
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter, resolvedType)
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())

    when:
      def operationCommand = new com.mangofactory.documentation.swagger.readers.parameter.ParameterMultiplesReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isAllowMultiple() == expected
    where:
      apiParamAnnotation                       | paramType        | expected
      [allowMultiple: {-> true }] as ApiParam  | null             | true
      [allowMultiple: {-> false }] as ApiParam | String[].class   | false
      [allowMultiple: {-> false }] as ApiParam | DummyClass.BusinessType[].class     | false
      null                                     | String[].class   | false
      null                                     | List.class       | false
      null                                     | Collection.class | false
      null                                     | Set.class        | false
      null                                     | Vector.class     | false
      null                                     | Object[].class   | false
      null                                     | Integer.class    | false
      null                                     | Iterable.class   | false
  }
}
