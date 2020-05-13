package springfox.bean.validators.plugins.parameter

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.bean.validators.plugins.ReflectionSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Negative
import javax.validation.constraints.NegativeOrZero
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

class ExpandedParameterMinMaxAnnotationPluginSpec
    extends Specification
    implements AnnotationsSupport, ReflectionSupport {
  @Shared
  def resolver = new TypeResolver()

  def "Always supported"() {
    expect:
    new ExpandedParameterMinMaxAnnotationPlugin().supports(types)

    where:
    types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Min/@Max annotations are reflected in the model for #fieldName"() {
    given:
    def sut = new ExpandedParameterMinMaxAnnotationPlugin()
    ParameterExpansionContext context = new ParameterExpansionContext(
        "Test",
        "",
        "",
        new ModelAttributeParameterMetadataAccessor(
            [named(Subject, fieldName).rawMember],
            resolver.resolve(Subject),
            fieldName),
        DocumentationType.SWAGGER_12,
        new ParameterBuilder())

    when:
    sut.apply(context)
    def property = context.parameterBuilder.build()

    then:
    def range = property.allowableValues as AllowableRangeValues
    range?.max == expectedMax
    range?.exclusiveMax == exclusiveMax
    range?.min == expectedMin
    range?.exclusiveMin == exclusiveMin

    where:
    fieldName           | expectedMin | exclusiveMin | expectedMax | exclusiveMax
    "noAnnotation"      | null        | null         | null        | null
    "onlyMin"           | "10.0"      | false        | null        | null
    "onlyMax"           | null        | null         | "20.0"      | false
    "both"              | "10.0"      | false        | "20.0"      | false
    "positive"          | "1.0"       | false        | null        | null
    "positiveOrZero"    | "0.0"       | false        | null        | null
    "negative"          | null        | null         | "-1.0"      | false
    "negativeOrZero"    | null        | null         | "0.0"       | false
    "positiveMax"       | "1.0"       | false        | "20.0"      | false
    "positiveOrZeroMax" | "0.0"       | false        | "20.0"      | false
    "negativeMin"       | "-30.0"     | false        | "-1.0"      | false
    "negativeOrZeroMin" | "-30.0"     | false        | "0.0"       | false
  }

  class Subject {
    String noAnnotation
    @Min(10L)
    String onlyMin
    @Max(20L)
    String onlyMax
    @Min(10L)
    @Max(20L)
    String both
    @Positive
    String positive
    @PositiveOrZero
    String positiveOrZero
    @Negative
    String negative
    @NegativeOrZero
    String negativeOrZero
    @Positive
    @Max(20L)
    String positiveMax
    @PositiveOrZero
    @Max(20L)
    String positiveOrZeroMax
    @Negative
    @Min(-30L)
    String negativeMin
    @NegativeOrZero
    @Min(-30L)
    String negativeOrZeroMin
  }
}
