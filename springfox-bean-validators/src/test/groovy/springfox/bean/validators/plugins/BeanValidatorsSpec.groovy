package springfox.bean.validators.plugins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.databind.type.TypeFactory
import spock.lang.Specification
import spock.lang.Unroll
import springfox.bean.validators.plugins.models.BeanValidatorsTestModel
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.builders.PropertySpecificationBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import java.lang.reflect.AnnotatedElement

class BeanValidatorsSpec extends Specification {
  def "Cannot instantiate"() {
    when:
    new Validators()

    then:
    thrown(UnsupportedOperationException)
  }

  def "When AnnotatedElement is null"() {
    when:
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        new PropertySpecificationBuilder(""),
        (AnnotatedElement) null,
        new TypeResolver(),
        Mock(ModelContext))
    def annotation = Validators.extractAnnotation(context, NotNull)

    then:
    !annotation.isPresent()
  }

  def "When BeanPropertyDefinition has no field/getter"() {
    when:
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        Mock(BeanPropertyDefinition),
        new TypeResolver(),
        Mock(ModelContext),
        new PropertySpecificationBuilder(""))
    def annotation = Validators.extractAnnotation(context, NotNull)

    then:
    !annotation.isPresent()
  }

  @Unroll
  def "@NotNull annotations are reflected in the model #propertyName that are AnnotatedElements"() {
    given:
    def property = BeanValidatorsTestModel.getDeclaredField(propertyName)
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        new PropertySpecificationBuilder(propertyName),
        property,
        new TypeResolver(),
        Mock(ModelContext))

    when:
    def annotation = Validators.extractAnnotation(context, NotNull)

    then:
    annotation.isPresent() == present

    where:
    propertyName                       | present
    "noAnnotation"                     | false
    "annotationOnField"                | true
    "annotationOnGetter"               | false
    "compositeAnnotationOnField"       | true
    "compositeAnnotationOnGetter"      | false
    "extraCompositeAnnotationOnField"  | true
    "extraCompositeAnnotationOnGetter" | false
  }

  @Unroll
  def "@NotNull annotations are reflected in the model #propertyName that are BeanPropertyDefinitions"() {
    given:
    def property = beanProperty(propertyName)
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        property,
        new TypeResolver(),
        Mock(ModelContext),
        new PropertySpecificationBuilder(propertyName))

    when:
    def annotation = Validators.extractAnnotation(context, NotNull)

    then:
    annotation.isPresent() == present

    where:
    propertyName                       | present
    "noAnnotation"                     | false
    "annotationOnField"                | true
    "annotationOnGetter"               | true
    "compositeAnnotationOnField"       | true
    "compositeAnnotationOnGetter"      | true
    "extraCompositeAnnotationOnField"  | true
    "extraCompositeAnnotationOnGetter" | true
  }

  @Unroll
  def "Constraints can be overridden for #propertyName as an AnnotatedElement"() {
    given:
    def property = BeanValidatorsTestModel.getDeclaredField(propertyName)
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        new PropertySpecificationBuilder(propertyName),
        property,
        new TypeResolver(),
        Mock(ModelContext))

    when:
    def annotation = Validators.extractAnnotation(context, Pattern)

    then:
    def value = annotation.isPresent() ? annotation.get().regexp() : null
    value == overriddenValue

    where:
    propertyName       | overriddenValue
    "override"         | "overridden"
    "overrideOnGetter" | null
  }

  @Unroll
  def "Constraints can be overridden for #propertyName as a BeanPropertyDefinition"() {
    given:
    def property = beanProperty(propertyName)
    def context = new ModelPropertyContext(
        new ModelPropertyBuilder(),
        property,
        new TypeResolver(),
        Mock(ModelContext),
        new PropertySpecificationBuilder(propertyName))
    
    when:
    def annotation = Validators.extractAnnotation(context, Pattern)

    then:
    def value = annotation.isPresent() ? annotation.get().regexp() : null
    value == overriddenValue

    where:
    propertyName       | overriddenValue
    "override"         | "overridden"
    "overrideOnGetter" | "overriddenOnGetter"
  }

  def beanProperty(property) {
    def mapper = new ObjectMapper()
    mapper
        .serializationConfig
        .introspect(TypeFactory.defaultInstance().constructType(BeanValidatorsTestModel))
        .findProperties()
        .find { p -> (property == p.name) };
  }

}
