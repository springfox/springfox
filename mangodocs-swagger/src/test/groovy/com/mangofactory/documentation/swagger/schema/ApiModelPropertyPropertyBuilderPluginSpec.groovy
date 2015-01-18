package com.mangofactory.documentation.swagger.schema
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.mangofactory.documentation.schema.TypeWithAnnotatedGettersAndSetters
import com.mangofactory.documentation.schema.mixins.ConfiguredObjectMapperSupport
import com.mangofactory.documentation.service.model.builder.ModelPropertyBuilder
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
      ApiModelPropertyPropertyBuilderPlugin sut = new ApiModelPropertyPropertyBuilderPlugin()
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
    where:
      property    | required | description              | allowableValues
      "intProp"   | true     | "int Property Field"     | null
      "boolProp"  | false    | "bool Property Getter"   | null
      "enumProp"  | true     | "enum Prop Getter value" | ["ONE"]
  }

  def "ApiModelProperty annotated models get enriched with additional info given an annotated element" (){
    given:
      ApiModelPropertyPropertyBuilderPlugin sut = new ApiModelPropertyPropertyBuilderPlugin()
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
    where:
      property    | required | description              | allowableValues
      "intProp"   | null     | null                     | null
      "boolProp"  | false    | "bool Property Getter"   | null
      "enumProp"  | true     | "enum Prop Getter value" | ["ONE"]
  }

  BeanDescription beanDescription(Class<TypeWithAnnotatedGettersAndSetters> clazz) {
    def objectMapper = new ObjectMapper()
    objectMapper.getDeserializationConfig()
            .introspect(TypeFactory.defaultInstance().constructType(clazz))
  }

}
