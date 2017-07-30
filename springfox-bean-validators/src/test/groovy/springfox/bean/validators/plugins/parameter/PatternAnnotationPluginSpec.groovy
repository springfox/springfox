package springfox.bean.validators.plugins.parameter

import com.fasterxml.classmate.ResolvedType
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.AnnotationsSupport
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.GenericTypeNamingStrategy
import springfox.documentation.spi.service.contexts.DocumentationContext
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spi.service.contexts.ParameterContext


class PatternAnnotationPluginSpec extends Specification implements AnnotationsSupport {
  def "Always supported" () {
    expect:
      new PatternAnnotationPlugin().supports(types)
    where:
      types << [DocumentationType.SPRING_WEB, DocumentationType.SWAGGER_2, DocumentationType.SWAGGER_12]
  }

  @Unroll
  def "@Pattern annotations are reflected in the model properties that are AnnotatedElements for #propertyName"()  {
    given:
      def sut = new PatternAnnotationPlugin()
      def resolvedMethodParameter =
          new ResolvedMethodParameter(0, "", [annotation], Mock(ResolvedType))
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
      def regex = property.pattern as String
    regex == expectedPattern
    where:
    propertyName    | expectedPattern  | annotation
    "patternString" | "[a-zA-Z0-9_]"   | pattern("[a-zA-Z0-9_]")
    "null"          | null             | null
  }
}
