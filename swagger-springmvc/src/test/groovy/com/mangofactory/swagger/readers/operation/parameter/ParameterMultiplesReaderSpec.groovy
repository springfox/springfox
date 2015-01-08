package com.mangofactory.swagger.readers.operation.parameter
import com.fasterxml.classmate.ResolvedType
import com.mangofactory.service.model.builder.ParameterBuilder
import com.mangofactory.springmvc.plugins.ParameterContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.mixins.ModelProviderForServiceSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterMultiplesReaderSpec extends DocumentationContextSpec {
   def "param multiples for default reader"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterType() >> paramType
      ResolvedType resolvedType = paramType != null ? defaultValues.typeResolver.resolve(paramType) : null
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

  def "param multiples for swagger reader"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterType() >> paramType
      ResolvedType resolvedType = paramType != null ? defaultValues.typeResolver.resolve(paramType) : null
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter, resolvedType)
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())

    when:
      def operationCommand = new com.mangofactory.swagger.plugins.operation.parameter.ParameterMultiplesReader();
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
