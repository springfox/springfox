package springdox.documentation.spring.web.readers.parameter

import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestParam
import spock.lang.Unroll
import springdox.documentation.builders.ParameterBuilder
import springdox.documentation.service.ResolvedMethodParameter
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.schema.GenericTypeNamingStrategy
import springdox.documentation.spi.service.contexts.ParameterContext
import springdox.documentation.spring.web.mixins.ModelProviderForServiceSupport
import springdox.documentation.spring.web.mixins.RequestMappingSupport
import springdox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterReaderSpec extends DocumentationContextSpec {
   @Unroll("property #resultProperty expected: #expected")
   def "should set basic properties based on ApiParam annotation or a sensible default"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterAnnotation(RequestParam.class) >> reqParamAnnot
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation, reqParamAnnot]
      methodParameter."$springParameterMethod"() >> methodReturnValue
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(),
              context(), Mock(GenericTypeNamingStrategy))
    when:
      parameterPlugin.apply(parameterContext)

    then:
      parameterContext.parameterBuilder().build()."$resultProperty" == expected
    where:
      parameterPlugin                     | resultProperty | springParameterMethod | methodReturnValue | apiParamAnnotation                     | reqParamAnnot                          | expected
      new ParameterNameReader()           | 'name'         | 'getParameterName'    | 'someName'        | null                                   | null                                   | 'someName'
      new ParameterNameReader()           | 'name'         | 'none'                | 'any'             | apiParam ([name: {-> 'AnName' }])      | null                                   | 'param0'
      new ParameterNameReader()           | 'name'         | 'none'                | 'any'             | null                                   | reqParam([value: {-> 'ArName' }])      | 'ArName'
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | null                                   | null                                   | null
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | apiParam([defaultValue: {-> 'defl' }]) | null                                   | null
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | null                                   | reqParam([defaultValue: {-> 'defr' }]) | 'defr'
   }

  def "ParameterNameReader supports all documentationTypes"() {
    given:
      def sut = new ParameterNameReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "ParameterDefaultReader should work with any documentationType"() {
    given:
      def sut = new ParameterDefaultReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  def "ParameterTypeReader should work with any documentationType"() {
    given:
      def sut = new ParameterTypeReader()
    expect:
      sut.supports(DocumentationType.SPRING_WEB)
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
  }

  private ApiParam apiParam(Map closureMap) {
      closureMap as ApiParam
   }

   private RequestParam reqParam(Map closureMap) {
      closureMap as RequestParam
   }
}
