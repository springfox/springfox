package springfox.bean.validators.plugins.parameter

import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.bean.validators.plugins.ReflectionSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class ExpandedParameterPatternAnnotationPluginSpec
    extends Specification
    implements AnnotationsSupport, ReflectionSupport {
  def "Always supported" () {
    expect:
      new ExpandedParameterPatternAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Pattern annotations are reflected in the model properties that are AnnotatedElements for #fieldName"()  {
    given:
      def sut = new ExpandedParameterPatternAnnotationPlugin()
      ParameterExpansionContext context = new ParameterExpansionContext(
          "Test",
          "",
          named(Subject, fieldName),
          DocumentationType.SWAGGER_12,
          new ParameterBuilder())

    when:
      sut.apply(context)
      def property = context.parameterBuilder.build()
    then:
      property?.pattern == annotation?.regexp()
    where:
      fieldName       | annotation
      "noAnnotation"  | null
      "annotated"     | pattern("[a-zA-Z0-9_]")
  }

  class Subject {
    String noAnnotation
    @Pattern(regexp = "[a-zA-Z0-9_]")
    String annotated
  }
}
