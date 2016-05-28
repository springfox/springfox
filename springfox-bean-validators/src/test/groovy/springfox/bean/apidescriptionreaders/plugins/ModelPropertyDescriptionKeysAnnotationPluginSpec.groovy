package springfox.bean.apidescriptionreaders.plugins

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.google.common.base.Optional
import io.swagger.annotations.ApiModelProperty
import org.springframework.util.ReflectionUtils
import spock.lang.Specification
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

import java.lang.reflect.AnnotatedElement

class ModelPropertyDescriptionKeysAnnotationPluginSpec extends Specification {

    def "Plugin supports everything"() {
        given:
        def plugin = new ModelPropertyDescriptionKeysAnnotationPlugin()
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

    def "Apply plugin"() {
        given:
        def reader = Stub(ApiDescriptionPropertiesReader)
        def plugin = new ModelPropertyDescriptionKeysAnnotationPlugin()
        plugin.propertiesReader = reader
        def builder = Mock(ModelPropertyBuilder)
        def annotatedElement = Stub(AnnotatedElement)
        def context = Stub(ModelPropertyContext)
        def definition = Stub(BeanPropertyDefinition)


        reader.getProperty(expectedDescription) >> expectedDescription

        annotatedElement.getAnnotation(ApiModelProperty.class) >> field.getAnnotation(ApiModelProperty.class)

        context.getAnnotatedElement() >> Optional.fromNullable(annotatedElement)
        context.getBuilder() >> builder;

        when:
        plugin.apply(context)

        then:
        intercations * builder.description(expectedDescription)

        where:
        intercations | expectedDescription                     | field
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_ASSERT_FALSE)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_ASSERT_TRUE)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DECIMAL_MAX)
        1            | ""                                      | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DECIMAL_MIN)
        1            | AnnotatedFieldsHelperClass.FIELD_DIGITS | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_DIGITS)
        1            | AnnotatedFieldsHelperClass.FIELD_PAST   | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_FUTURE)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_MAX)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_MIN)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_NOT_NULL)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_NULL)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_PAST)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_PATTERN)
        0            | null                                    | ReflectionUtils.findField(AnnotatedFieldsHelperClass.class, AnnotatedFieldsHelperClass.FIELD_SIZE)
    }


}
