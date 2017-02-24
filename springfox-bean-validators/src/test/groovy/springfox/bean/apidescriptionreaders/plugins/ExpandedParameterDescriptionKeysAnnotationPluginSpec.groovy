package springfox.bean.apidescriptionreaders.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedField
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.schema.property.field.FieldProvider
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

class ExpandedParameterDescriptionKeysAnnotationPluginSpec extends Specification {

  @Unroll
  def "Plugin supports everything"() {
    given:
      def plugin = new ExpandedParameterDescriptionKeysAnnotationPlugin()
      plugin.propertiesReader = Mock(ApiDescriptionPropertiesReader.class)
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

  def "Extract validator from bean"() {
    given:
      def plugin = new ExpandedParameterDescriptionKeysAnnotationPlugin()
      plugin.propertiesReader = Mock(ApiDescriptionPropertiesReader.class)
    expect:
      ParameterExpansionContext context = new ParameterExpansionContext("", "", field, DocumentationType.SWAGGER_2, new ParameterBuilder())
      def optionalAnnotation = plugin.extractAnnotation(context)
      optionalAnnotation.isPresent() == containsValidator
    where:
      containsValidator | field
      true              | named(AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE)
      true              | named(AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE)
      true              | named(AnnotatedFieldsHelperClass.FIELD_DECIMAL_MAX)
      false             | named(AnnotatedFieldsHelperClass.FIELD_DECIMAL_MIN)
      false             | named(AnnotatedFieldsHelperClass.FIELD_DIGITS)
      false             | named(AnnotatedFieldsHelperClass.FIELD_FUTURE)
      false             | named(AnnotatedFieldsHelperClass.FIELD_MAX)
      false             | named(AnnotatedFieldsHelperClass.FIELD_MIN)
      false             | named(AnnotatedFieldsHelperClass.FIELD_NOT_NULL)
      false             | named(AnnotatedFieldsHelperClass.FIELD_NULL)
      false             | named(AnnotatedFieldsHelperClass.FIELD_PAST)
      false             | named(AnnotatedFieldsHelperClass.FIELD_PATTERN)
      false             | named(AnnotatedFieldsHelperClass.FIELD_SIZE)
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
      def reader = Stub(ApiDescriptionPropertiesReader.class)
      def plugin = new ExpandedParameterDescriptionKeysAnnotationPlugin()
      plugin.propertiesReader = reader
      ParameterBuilder paramBuilder = Mock(ParameterBuilder.class)
      reader.getProperty(expectedDescription) >> expectedDescription

    when:
      ParameterExpansionContext context = new ParameterExpansionContext("", "", field, DocumentationType.SWAGGER_2, paramBuilder)
      plugin.apply(context)

    then:
      intercations * paramBuilder.description(expectedDescription)

    where:
      intercations | expectedDescription                           | field
      1            | ""                                            | named(AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE)
      1            | AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE | named(AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE)
      1            | AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE  | named(AnnotatedFieldsHelperClass.FIELD_DECIMAL_MAX)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_DECIMAL_MIN)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_DIGITS)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_FUTURE)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_MAX)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_MIN)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_NOT_NULL)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_NULL)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_PAST)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_PATTERN)
      0            | null                                          | named(AnnotatedFieldsHelperClass.FIELD_SIZE)
  }


}
