package springfox.documentation.spring.web.readers.parameter

import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.service.contexts.ParameterContext
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

import java.lang.annotation.Annotation

@Mixin([RequestMappingSupport])
class ParameterRequiredReaderSpec extends DocumentationContextSpec {

  def "parameters required using default reader"() {
    given:
      MethodParameter methodParameter = Mock(MethodParameter)
      methodParameter.getParameterAnnotations() >> (paramAnnotations as Annotation[])
      methodParameter.getParameterType() >> Object.class
      methodParameter.getMethodAnnotation(PathVariable.class) >> paramAnnotations.find { it instanceof PathVariable }
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
              context(), Mock(GenericTypeNamingStrategy))
    when:
      def operationCommand = new ParameterRequiredReader();
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().isRequired() == expected
    where:
      paramAnnotations                                                                  | expected
      [[required: { -> false }] as ApiParam, [required: { -> false }] as PathVariable]  | true
      [[required: { -> false }] as ApiParam, [required: { -> false }] as RequestHeader] | false
      [[required: { -> true }] as RequestHeader]                                        | true
      [[required: { -> false }] as RequestHeader]                                       | false
      [[required: { -> true }] as ApiParam]                                             | false
      [[required: { -> false }] as ApiParam]                                            | false
      [[required: { -> true }] as RequestParam]                                         | true
      [[required: { -> false }] as RequestParam]                                        | false
      [[required: { -> true }] as ApiParam, [required: { -> false }] as RequestParam]   | false
      [[required: { -> false }] as ApiParam, [required: { -> true }] as RequestParam]   | true
      []                                                                                | false
      [null]                                                                            | false
  }

  def "should detect java.util.Optional parameters"() {
    given:
      MethodParameter methodParameter = Mock(MethodParameter)
      methodParameter.getParameterAnnotations() >> (paramAnnotations as Annotation[])
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      Class<?> fakeOptionalClass = new FakeOptional().class
      fakeOptionalClass.name = "java.util.Optional"
      methodParameter.getParameterType() >> fakeOptionalClass
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
              context(), Mock(GenericTypeNamingStrategy))

    when:
      def operationCommand = new ParameterRequiredReader();
      operationCommand.apply(parameterContext)
    then:
      !parameterContext.parameterBuilder().build().isRequired()
    where:
      paramAnnotations << [
              [[required: { -> true }] as RequestHeader],
              [[required: { -> false }] as RequestHeader],
              [[required: { -> true }] as RequestParam],
              [[required: { -> false }] as RequestParam],
      ]
  }

  def "Supports all documentation types"() {
    given:
      def sut = new ParameterRequiredReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }
}

class FakeOptional {}
