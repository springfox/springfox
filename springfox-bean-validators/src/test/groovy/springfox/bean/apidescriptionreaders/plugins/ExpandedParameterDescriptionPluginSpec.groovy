package springfox.bean.apidescriptionreaders.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

import static springfox.bean.apidescriptionreaders.plugins.AnnotatedFieldsHelperClass.*

class ExpandedParameterDescriptionPluginSpec extends Specification {

  @Unroll
  def "Plugin supports everything"() {
    given:
      def plugin = new ExpandedParameterDescriptionPlugin(new DescriptionResolver(new MockEnvironment()))
    expect:
      supported == plugin.supports(delimiter)
    where:
      supported | delimiter
      true      | DocumentationType.SWAGGER_12
      true      | DocumentationType.SWAGGER_2
      true      | DocumentationType.SPRING_WEB
      true      | new DocumentationType("Everything", "is supported")
      true      | null
  }

  @Unroll
  def "Extract validator from bean property #description"() {
    given:
      def plugin = new ExpandedParameterDescriptionPlugin(
          new DescriptionResolver(
              new MockEnvironment()))
      ParameterExpansionContext context = new ParameterExpansionContext(
          "",
          "",
          field,
          DocumentationType.SWAGGER_2,
          new ParameterBuilder())

    and:
      plugin.apply(context)

    expect:
      context.parameterBuilder.build().description == description

    where:
      description         | field
      '${assertFalse}'    | named(FIELD_ASSERT_FALSE)
      FIELD_ASSERT_TRUE   | named(FIELD_ASSERT_TRUE)
      FIELD_DECIMAL_MAX   | named(FIELD_DECIMAL_MAX)
      null                | named(FIELD_DECIMAL_MIN)
      null                | named(FIELD_DIGITS)
      null                | named(FIELD_FUTURE)
      null                | named(FIELD_MAX)
      null                | named(FIELD_MIN)
      null                | named(FIELD_NOT_NULL)
      null                | named(FIELD_NULL)
      null                | named(FIELD_PAST)
      null                | named(FIELD_PATTERN)
      null                | named(FIELD_SIZE)
  }

  def named(String name) {
    def resolver = new TypeResolver()
    FieldProvider fieldProvider = new FieldProvider(resolver)
    for (ResolvedField field : fieldProvider.in(resolver.resolve(AnnotatedFieldsHelperClass))) {
      if (field.name == name) {
        return field
      }
    }
  }

  def "Apply plugin"() {
    given:
      def sut = new ExpandedParameterDescriptionPlugin(
          new DescriptionResolver(
              new MockEnvironment()))
      ParameterBuilder paramBuilder = new ParameterBuilder()

    when:
      ParameterExpansionContext context = new ParameterExpansionContext(
          "",
          "",
          field,
          DocumentationType.SWAGGER_2,
          paramBuilder)
      sut.apply(context)

    then:
      expectedDescription == context.parameterBuilder.build().description

    where:
      expectedDescription| field
      '${assertFalse}'   | named(FIELD_ASSERT_FALSE)
      FIELD_ASSERT_TRUE  | named(FIELD_ASSERT_TRUE)
      FIELD_DECIMAL_MAX  | named(FIELD_DECIMAL_MAX)
      null               | named(FIELD_DECIMAL_MIN)
      null               | named(FIELD_DIGITS)
      null               | named(FIELD_FUTURE)
      null               | named(FIELD_MAX)
      null               | named(FIELD_MIN)
      null               | named(FIELD_NOT_NULL)
      null               | named(FIELD_NULL)
      null               | named(FIELD_PAST)
      null               | named(FIELD_PATTERN)
      null               | named(FIELD_SIZE)
  }
}

