package springfox.bean.validators.plugins.parameter

import com.fasterxml.classmate.TypeResolver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.bean.validators.plugins.ReflectionSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext
import springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterMetadataAccessor

import javax.validation.constraints.NotNull

class ExpandedParameterNotNullAnnotationPluginSpec
    extends Specification
    implements AnnotationsSupport, ReflectionSupport {
  @Shared
  def resolver = new TypeResolver()

  def "Always supported"() {
    expect:
    new ExpandedParameterNotNullAnnotationPlugin().supports(types)

    where:
    types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@NotNull annotations are reflected in the model for #fieldName"() {
    given:
    def sut = new ExpandedParameterNotNullAnnotationPlugin()
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
    property.required == required

    where:
    fieldName      | required
    "noAnnotation" | false
    "annotated"    | true
  }

  class Subject {
    String noAnnotation
    @NotNull
    String annotated
  }
}
