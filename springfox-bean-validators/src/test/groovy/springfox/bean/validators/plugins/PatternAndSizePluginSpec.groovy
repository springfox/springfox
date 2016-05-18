package springfox.bean.validators.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.models.PatternAndSizeTestModel
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.service.AllowableRangeValues
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

/**
 * @author : ashutosh 
 * 18/05/2016
 */
class PatternAndSizePluginSpec extends Specification{

  @Unroll
  def "@Pattern annotations are reflected in the model properties that are AnnotatedElements"()  {
    given:
    def pat = new PatternAnnotationPlugin()
    def sat = new SizeAnnotationPlugin()
    def element = PatternAndSizeTestModel.getDeclaredField(propertyName)
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        element,
        new TypeResolver(),
        DocumentationType.SWAGGER_12)
    when:
    sat.apply(context)
    pat.apply(context)
    def property = context.builder.build()
    then:
    def range = property.allowableValues as AllowableRangeValues
    range?.max == expectedMax
    range?.min == expectedMin
    property.getPattern() == pattern
    where:
    propertyName                   | pattern        | expectedMin   | expectedMax
    "propertyString"               | "[a-zA-Z0-9_]" | "3"           | "5"
    "getterString"                 | null           | null          | null
  }

  @Unroll
  def "@Pattern annotations are reflected in the model properties that are BeanPropertyDefinitions"()  {
    given:
    def pat = new PatternAnnotationPlugin()
    def sat = new SizeAnnotationPlugin()
    def beanProperty = beanProperty(propertyName)
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        beanProperty,
        new TypeResolver(),
        DocumentationType.SWAGGER_12)
    when:
    sat.apply(context)
    pat.apply(context)
    def property = context.builder.build()
    then:
    property.getPattern() == pattern
    where:
    propertyName                   | pattern        | expectedMin   | expectedMax
    "propertyString"               | "[a-zA-Z0-9_]" | "3"           | "5"
    "getterString"                 | "[A-Z]"        | "1"           | "4"

  }

  def beanProperty(property) {
    def mapper = new ObjectMapper()
    mapper
        .serializationConfig
        .introspect(TypeFactory.defaultInstance().constructType(PatternAndSizeTestModel))
        .findProperties()
        .find { p -> property.equals(p.name) };
  }
}
