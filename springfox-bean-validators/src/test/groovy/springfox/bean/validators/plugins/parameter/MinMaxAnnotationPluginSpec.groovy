package springfox.bean.validators.plugins.parameter

import com.fasterxml.classmate.ResolvedType
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext


class MinMaxAnnotationPluginSpec extends Specification implements AnnotationsSupport {
  def "Always supported" () {
    expect:
      new MinMaxAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Min/@Max annotations are reflected in the model for #scenario"()  {
    given:
      def sut = new MinMaxAnnotationPlugin()
      def resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", annotations, Mock(ResolvedType))
      ParameterContext context = new ParameterContext(
          resolvedMethodParameter,
          new ParameterBuilder(),
          Mock(DocumentationContext),
          Mock(GenericTypeNamingStrategy),
          Mock(OperationContext))

    when:
      sut.apply(context)
      def property = context.parameterBuilder().build()
    then:
      def range = property.allowableValues as AllowableRangeValues
      range?.max == expectedMax
      range?.exclusiveMax == exclusiveMax
      range?.min == expectedMin
      range?.exclusiveMin == exclusiveMin
    where:
      scenario            | expectedMin | exclusiveMin | expectedMax | exclusiveMax | annotations
      "noAnnotation"      | null        | null         | null        | null         | []
      "onlyMin"           | "10.0"      | false        | null        | null         | [min(10L)]
      "onlyMax"           | null        | null         | "20.0"      | false        | [max(20L)]
      "both"              | "10.0"      | false        | "20.0"      | false        | [min(10L), max(20L)]
      "positive"          | "1.0"       | false        | null        | null         | [positive()]
      "positiveOrZero"    | "0.0"       | false        | null        | null         | [positiveOrZero()]
      "negative"          | null        | null         | "-1.0"      | false        | [negative()]
      "negativeOrZero"    | null        | null         | "0.0"       | false        | [negativeOrZero()]
      "positiveMax"       | "1.0"       | false        | "20.0"      | false        | [positive(), max(20L)]
      "positiveOrZeroMax" | "0.0"       | false        | "20.0"      | false        | [positiveOrZero(), max(20L)]
      "negativeMin"       | "-30.0"     | false        | "-1.0"      | false        | [negative(), min(-30L)]
      "negativeOrZeroMin" | "-30.0"     | false        | "0.0"       | false        | [negativeOrZero(), min(-30L)]
  }
}
