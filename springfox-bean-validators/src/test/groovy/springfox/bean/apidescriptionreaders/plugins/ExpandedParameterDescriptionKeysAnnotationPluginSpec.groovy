package springfox.bean.apidescriptionreaders.plugins

import org.springframework.util.ReflectionUtils
import spock.lang.Specification
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterExpansionContext

class ExpandedParameterDescriptionKeysAnnotationPluginSpec extends Specification {


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
        true              | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE)
        true              | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE)
        true              | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DECIMAL_MAX)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DECIMAL_MIN)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DIGITS)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_FUTURE)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_MAX)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_MIN)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_NOT_NULL)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_NULL)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_PAST)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_PATTERN)
        false             | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_SIZE)
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
        1            | ""                                            | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE)
        1            | AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE)
        1            | AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE  | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DECIMAL_MAX)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DECIMAL_MIN)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DIGITS)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_FUTURE)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_MAX)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_MIN)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_NOT_NULL)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_NULL)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_PAST)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_PATTERN)
        0            | null                                          | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_SIZE)
    }


}
