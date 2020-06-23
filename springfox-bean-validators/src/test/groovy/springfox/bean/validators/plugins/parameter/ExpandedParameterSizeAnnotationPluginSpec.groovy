package springfox.bean.validators.plugins.parameter

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.bean.validators.plugins.ReflectionSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor

import javax.validation.constraints.Size

class ExpandedParameterSizeAnnotationPluginSpec
    extends Specification
    implements AnnotationsSupport, ReflectionSupport {
  @Shared
  def resolver = new TypeResolver()

  def "Always supported"() {
    expect:
    new ExpandedParameterSizeAnnotationPlugin().supports(types)

    where:
    types << [
        DocumentationType.SPRING_WEB,
        DocumentationType.SWAGGER_2,
        DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Size annotations are reflected for #fieldName"() {
    given:
    def sut = new ExpandedParameterSizeAnnotationPlugin()
    ParameterExpansionContext context = new ParameterExpansionContext(
        "Test",
        "",
        "",
        new ModelAttributeParameterMetadataAccessor(
            [named(Subject, fieldName).rawMember],
            resolver.resolve(Subject),
            fieldName),
        DocumentationType.SWAGGER_12,
        new ParameterBuilder(),
        new RequestParameterBuilder())

    when:
    sut.apply(context)
    def property = context.parameterBuilder.build()

    then:
    def range = property.allowableValues as AllowableRangeValues
    if (range != null) {
      range.max == annotation == null ? null : annotation.min().toString()
      range.min == annotation == null ? null : annotation.max().toString()
    } else {
      range == null
    }

    where:
    fieldName      | annotation
    "noAnnotation" | null
    "zeroMax"      | size(0, Integer.MAX_VALUE)
    "zeroTen"      | size(0, 10)
    "tenZero"      | size(10, 0)
    "maxZero"      | size(Integer.MAX_VALUE, 0)
    "bothZero"     | size(0, 0)
    "bothNegative" | size(-10, -5)
    "bothMax"      | size(Integer.MAX_VALUE, Integer.MAX_VALUE)
  }

  class Subject {
    String noAnnotation
    @Size(min = 0, max = Integer.MAX_VALUE)
    String zeroMax
    @Size(min = 0, max = 10)
    String zeroTen
    @Size(min = 10, max = 0)
    String tenZero
    @Size(min = Integer.MAX_VALUE, max = 0)
    String maxZero
    @Size(min = 0, max = 0)
    String bothZero
    @Size(min = -10, max = -5)
    String bothNegative
    @Size(min = Integer.MAX_VALUE, max = Integer.MAX_VALUE)
    String bothMax
  }
}
