package springdox.documentation.swagger.readers.parameter

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import springdox.documentation.builders.ParameterBuilder
import springdox.documentation.schema.DefaultGenericTypeNamingStrategy
import springdox.documentation.service.ResolvedMethodParameter
import springdox.documentation.spi.service.contexts.ParameterContext
import springdox.documentation.spring.web.dummy.DummyClass
import springdox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterMultiplesReaderSpec extends DocumentationContextSpec {
  def "param multiples for swagger reader"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterType() >> paramType
      ResolvedType resolvedType = paramType != null ? new TypeResolver().resolve(paramType) : null
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter, resolvedType)
      def genericNamingStrategy = new DefaultGenericTypeNamingStrategy()
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context(), genericNamingStrategy)

    when:
      def operationCommand = new ParameterMultiplesReader();
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
