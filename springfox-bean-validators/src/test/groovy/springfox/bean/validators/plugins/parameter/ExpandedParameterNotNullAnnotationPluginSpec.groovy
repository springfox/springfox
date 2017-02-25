package springfox.bean.validators.plugins.parameter

import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.bean.validators.plugins.ReflectionSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

import javax.validation.constraints.NotNull

class ExpandedParameterNotNullAnnotationPluginSpec
    extends Specification
    implements AnnotationsSupport, ReflectionSupport {
  def "Always supported" () {
    expect:
      new ExpandedParameterNotNullAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Min/@Max annotations are reflected in the model for #fieldName"()  {
    given:
      def sut = new ExpandedParameterNotNullAnnotationPlugin()
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
      property.required == required
    where:
      fieldName       | required
      "noAnnotation"  | false
      "annotated"     | true
  }

  class Subject {
    String noAnnotation
    @NotNull
    String annotated
  }
}
