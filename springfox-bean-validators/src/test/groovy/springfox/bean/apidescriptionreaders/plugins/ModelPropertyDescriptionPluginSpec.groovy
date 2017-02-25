package springfox.bean.apidescriptionreaders.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import org.springframework.mock.env.MockEnvironment
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

import static springfox.bean.apidescriptionreaders.plugins.AnnotatedFieldsHelperClass.*

class ModelPropertyDescriptionPluginSpec extends Specification {

  def "Plugin supports everything"() {
    given:
      def plugin = new ModelPropertyDescriptionPlugin(
          new DescriptionResolver(
              new MockEnvironment()))
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
  def "Apply plugin #field.name"() {
    given:
      def env = new MockEnvironment()
      ["digits": "digits description", "future": "future description"].each {
        env.withProperty(it.key, it.value)
      }
      def sut = new ModelPropertyDescriptionPlugin(
        new DescriptionResolver(env))
      def propertyBuilder = new ModelPropertyBuilder()
      def context = new ModelPropertyContext(
          propertyBuilder,
          field.getRawMember(),
          new TypeResolver(),
          DocumentationType.SWAGGER_12)

    when:
      sut.apply(context)
      def property = propertyBuilder.build()
    then:
      property.description == expectedDescription

    where:
      expectedDescription    | field
      null                   | named(FIELD_ASSERT_FALSE)
      null                   | named(FIELD_ASSERT_TRUE)
      null                   | named(FIELD_DECIMAL_MAX)
      ""                     | named(FIELD_DECIMAL_MIN)
      "digits description"   | named(FIELD_DIGITS)
      "future description"   | named(FIELD_FUTURE)
      null                   | named(FIELD_MAX)
      null                   | named(FIELD_MIN)
      null                   | named(FIELD_NOT_NULL)
      null                   | named(FIELD_NULL)
      null                   | named(FIELD_PAST)
      null                   | named(FIELD_PATTERN)
      null                   | named(FIELD_SIZE)
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
}
