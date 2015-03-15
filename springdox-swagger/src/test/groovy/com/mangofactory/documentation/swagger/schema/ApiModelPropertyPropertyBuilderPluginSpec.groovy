package com.mangofactory.documentation.swagger.schema
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.mangofactory.documentation.schema.TypeWithAnnotatedGettersAndSetters
import com.mangofactory.documentation.schema.mixins.ConfiguredObjectMapperSupport
import com.mangofactory.documentation.builders.ModelPropertyBuilder
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.schema.contexts.ModelPropertyContext
import spock.lang.Specification

@Mixin(ConfiguredObjectMapperSupport)
class ApiModelPropertyPropertyBuilderPluginSpec extends Specification {
  BeanDescription beanDescription

  def setup() {
    beanDescription = beanDescription(TypeWithAnnotatedGettersAndSetters)
  }

  def "ApiModelProperty annotated models get enriched with additional info given a bean property" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property },
              DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == allowableValues
      enriched.isRequired() == required
      enriched.description == description
      !enriched.isHidden()
    where:
      property    | required | description              | allowableValues
      "intProp"   | true     | "int Property Field"     | null
      "boolProp"  | false    | "bool Property Getter"   | null
      "enumProp"  | true     | "enum Prop Getter value" | ["ONE"]
  }

  def "ApiModelProperty annotated models get enriched with additional info given an annotated element" (){
    given:
      ApiModelPropertyPropertyBuilder sut = new ApiModelPropertyPropertyBuilder()
      def properties = beanDescription.findProperties()
      def context = new ModelPropertyContext(new ModelPropertyBuilder(),
              properties.find { it.name == property }.getter.annotated,
              DocumentationType.SWAGGER_12)
    when:
      sut.apply(context)
    and:
      def enriched = context.getBuilder().build()
    then:
      enriched.allowableValues?.values == allowableValues
      enriched.isRequired() == required
      enriched.description == description
      !enriched.isHidden()
    where:
      property    | required | description              | allowableValues
      "intProp"   | null     | null                     | null
      "boolProp"  | false    | "bool Property Getter"   | null
      "enumProp"  | true     | "enum Prop Getter value" | ["ONE"]
  }

//  def "Detects properties annotated as hidden"() {
//    given:
//      Class typeToTest = typeForTestingAnnotatedGettersAndSetter()
//      def method = accessorMethod(typeToTest, "getHiddenProp")
//      def propertyDefinition = beanPropertyDefinition(typeToTest, "getHiddenProp")
//
//      ObjectMapper mapper = new ObjectMapper()
//      String propName = name(propertyDefinition, true, new ObjectMapperBeanPropertyNamingStrategy(mapper))
//      def sut = new BeanModelProperty(propName, propertyDefinition, method, isGetter(method.getRawMember()),
//              new TypeResolver(), new AlternateTypeProvider())
//
//    expect:
//      sut.isHidden()
//  }

  BeanDescription beanDescription(Class<TypeWithAnnotatedGettersAndSetters> clazz) {
    def objectMapper = new ObjectMapper()
    objectMapper.getDeserializationConfig()
            .introspect(TypeFactory.defaultInstance().constructType(clazz))
  }

}
