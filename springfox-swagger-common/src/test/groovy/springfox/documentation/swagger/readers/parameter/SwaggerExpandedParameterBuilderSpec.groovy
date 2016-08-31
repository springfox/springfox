package springfox.documentation.swagger.readers.parameter

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import io.swagger.annotations.ApiModelProperty
import io.swagger.annotations.ApiParam
import spock.lang.Specification
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

class SwaggerExpandedParameterBuilderSpec extends Specification {
  def "Swagger parameter expander expands as expected" () {
    given:
      SwaggerExpandedParameterBuilder sut = new SwaggerExpandedParameterBuilder()
    and:
      ParameterExpansionContext context = new ParameterExpansionContext("Test", "", field,
          DocumentationType.SWAGGER_12, new ParameterBuilder())
    when:
      sut.apply(context)
      def param = context.parameterBuilder.build()
    then:
      param != null //TODO: add more fidelity to this test
    and:
      sut.supports(DocumentationType.SWAGGER_12)
      sut.supports(DocumentationType.SWAGGER_2)
      !sut.supports(DocumentationType.SPRING_WEB)
    where:
      field << [named("a"), named("b"), named("c"), named("d"), named("f") ]
  }

  def named(String name) {
    def resolver = new TypeResolver()
    FieldProvider fieldProvider = new FieldProvider(resolver)
    for (ResolvedField field : fieldProvider.in(resolver.resolve(A))) {
      if (field.name == name) {
        return field
      }
    }
  }

  class A {
    public String a;

    @ApiModelProperty(name = "b")
    public String b;

    @ApiParam(name = "c", allowableValues = "a, b, c")
    public String c;

    @ApiModelProperty(name = "d")
    public D d;

    @ApiModelProperty(name = "f")
    public ExampleEnum f;
  }

  class D {
    @ApiModelProperty(name = "e")
    public String e;
  }
}
