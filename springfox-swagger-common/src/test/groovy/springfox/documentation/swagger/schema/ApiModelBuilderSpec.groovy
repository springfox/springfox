package springfox.documentation.swagger.schema

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiModel
import spock.lang.Shared
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.SchemaSpecification
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

class ApiModelBuilderSpec extends SchemaSpecification {
  @Shared def resolver = new TypeResolver()

  def "Api model builder parses ApiModel annotation as expected" () {
    given:
      ApiModelBuilder sut = new ApiModelBuilder(resolver, modelProvider)
      ModelContext context = ModelContext.inputParam(type, documentationType,
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

  def "Api model builder parses ApiModel annotation discriminator as expected" () {
    given:
      ApiModelBuilder sut = new ApiModelBuilder(resolver, modelProvider)
      ModelContext context = ModelContext.inputParam(type, documentationType,
          new AlternateTypeProvider([]), new DefaultGenericTypeNamingStrategy())
    when:
      sut.apply(context)
    then:
      context.builder.build().discriminator == expected
    where:
      type                | expected
      String              | null
      DiscriminatorTest   | "discriminator"

  }

  @ApiModel(discriminator = "discriminator")
  class DiscriminatorTest {

  }

  def "Api model builder parses ApiModel annotation parent as expected" () {
    given:
      ApiModelBuilder sut = new ApiModelBuilder(resolver, modelProvider)
      ModelContext context = ModelContext.inputParam(type, documentationType,
          new AlternateTypeProvider([]), new DefaultGenericTypeNamingStrategy())
    when:
      sut.apply(context)
    and:
      def enriched = context.builder.build()
    then:
      enriched.parent.name == expected
    where:
      type                | expected
      ParentTest          | "DiscriminatorTest"

  }

  @ApiModel(parent = DiscriminatorTest.class)
  class ParentTest {

  }

  def "Api model builder parses ApiModel annotation without parent" () {
    given:
      ApiModelBuilder sut = new ApiModelBuilder(resolver, modelProvider)
      ModelContext context = ModelContext.inputParam(type, documentationType,
        new AlternateTypeProvider([]), new DefaultGenericTypeNamingStrategy())
    when:
      sut.apply(context)
    and:
      def enriched = context.builder.build()
    then:
      enriched.parent == expected
    where:
      type                | expected
      AnnotatedTest       | null
  }
}
