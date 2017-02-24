package springfox.bean.apidescriptionreaders.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

class ModelPropertyDescriptionKeysAnnotationPluginSpec extends Specification {

  def "Plugin supports everything"() {
    given:
      def plugin = new ModelPropertyDescriptionKeysAnnotationPlugin(Mock(ApiDescriptionPropertiesReader))
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
      def reader = Stub(ApiDescriptionPropertiesReader)
      def plugin = new ModelPropertyDescriptionKeysAnnotationPlugin(reader)
      def builder = Mock(ModelPropertyBuilder)
      def context = new ModelPropertyContext(
          builder,
          field.getRawMember(),
          new TypeResolver(),
          DocumentationType.SWAGGER_12)
      reader.getProperty(expectedDescription) >> expectedDescription

    when:
      plugin.apply(context)

    then:
      intercations * builder.description(expectedDescription)

    where:
      intercations | expectedDescription                     | field
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_DECIMAL_MAX)
      1            | ""                                      | named(AnnotatedFieldsHelperClass.FIELD_DECIMAL_MIN)
      1            | AnnotatedFieldsHelperClass.FIELD_DIGITS | named(AnnotatedFieldsHelperClass.FIELD_DIGITS)
      1            | AnnotatedFieldsHelperClass.FIELD_PAST   | named(AnnotatedFieldsHelperClass.FIELD_FUTURE)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_MAX)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_MIN)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_NOT_NULL)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_NULL)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_PAST)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_PATTERN)
      0            | null                                    | named(AnnotatedFieldsHelperClass.FIELD_SIZE)
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
