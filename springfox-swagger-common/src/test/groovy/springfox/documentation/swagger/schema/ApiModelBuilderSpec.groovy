package springfox.documentation.swagger.schema

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiModel
import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

class ApiModelBuilderSpec extends Specification {
  @Shared def resolver = new TypeResolver()

  def "Api model builder parses ApiModel annotation as expected" () {
    given:
      ApiModelBuilder sut = new ApiModelBuilder(resolver)
      ModelContext context = ModelContext.inputParam(type, DocumentationType.SWAGGER_12,
          new AlternateTypeProvider([]), new DefaultGenericTypeNamingStrategy())
    when:
      sut.apply(context)
    then:
      context.builder.build().description == expected
    where:
      type                | expected
      String              | null
      AnnotatedTest       | "description"

  }

  @ApiModel(description = "description")
  class AnnotatedTest {

  }
}
