package com.mangofactory.documentation.spring.web.readers.parameter
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.builders.ParameterBuilder
import com.mangofactory.documentation.spi.service.contexts.ParameterContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.dummy.DummyClass
import com.mangofactory.documentation.spring.web.mixins.ModelProviderForServiceSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.service.ResolvedMethodParameter
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterMultiplesReaderSpec extends DocumentationContextSpec {
   def "param multiples for default reader"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterType() >> paramType
      ResolvedType resolvedType = paramType != null ? new TypeResolver().resolve(paramType) : null
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter, resolvedType)
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())

    when:
      def operationCommand = new ParameterMultiplesReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isAllowMultiple() == expected
    where:
      apiParamAnnotation                       | paramType        | expected
      [allowMultiple: {-> true }] as ApiParam  | null             | false
      [allowMultiple: {-> false }] as ApiParam | String[].class   | true
      [allowMultiple: {-> false }] as ApiParam | DummyClass.BusinessType[].class     | true
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
