package springdox.documentation.swagger.readers.parameter

import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import spock.lang.Unroll
import springdox.documentation.builders.ParameterBuilder
import springdox.documentation.service.AllowableListValues
import springdox.documentation.service.AllowableRangeValues
import springdox.documentation.service.ResolvedMethodParameter
import springdox.documentation.spi.service.contexts.ParameterContext
import springdox.documentation.spring.web.dummy.DummyClass
import springdox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterAllowableReaderSpec extends DocumentationContextSpec {

  def "enum types"() {
    given:
      MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 0)
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())

    when:
      ParameterAllowableReader operationCommand = new ParameterAllowableReader();
      operationCommand.apply(parameterContext)
      AllowableListValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableListValues
    then:
      allowableValues != null
      allowableValues.getValueType() == "LIST"
      allowableValues.getValues() == ["PRODUCT", "SERVICE"]
    where:
      handlerMethod                                                                    | expected
      dummyHandlerMethod('methodWithSingleEnum', DummyClass.BusinessType.class)        | AllowableListValues
      dummyHandlerMethod('methodWithSingleEnumArray', DummyClass.BusinessType[].class) | AllowableListValues
  }

  @Unroll
  def "Api annotation with list type"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation]
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())

    when:
      ParameterAllowableReader operationCommand = new ParameterAllowableReader();
      operationCommand.apply(parameterContext)
      AllowableListValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableListValues
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
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation]
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())

    when:
      ParameterAllowableReader operationCommand = new ParameterAllowableReader();
      operationCommand.apply(parameterContext)
      AllowableRangeValues allowableValues = parameterContext.parameterBuilder().build().allowableValues as AllowableRangeValues
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
