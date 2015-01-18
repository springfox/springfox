package com.mangofactory.documentation.schema
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.mangofactory.documentation.schema.mixins.ModelPropertyLookupSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import com.mangofactory.documentation.schema.property.ObjectMapperBeanPropertyNamingStrategy
import spock.lang.Specification

@Mixin([ModelPropertyLookupSupport, TypesForTestingSupport])
class ObjectMapperNamingStrategySpec extends Specification {

  def "rename without setting an strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectMapperBeanPropertyNamingStrategy sut = new ObjectMapperBeanPropertyNamingStrategy(objectMapper);
      def beanPropertyDefinition = beanPropertyDefinition(simpleType(), beanAccessorMethod)

    expect:
      sut.nameForSerialization(beanPropertyDefinition) == name
      sut.nameForDeserialization(beanPropertyDefinition) == name

    where:
      beanAccessorMethod     | name
      "getAnObject"          | "anObject"
      "setaByte"             | "aByte"
      "getAnObjectBoolean"   | "anObjectBoolean"
      "setDate"              | "date"
  }

  def "rename setting snake_case strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
      ObjectMapperBeanPropertyNamingStrategy sut = new ObjectMapperBeanPropertyNamingStrategy(objectMapper);
      def beanPropertyDefinition = beanPropertyDefinition(simpleType(), beanAccessorMethod)

    expect:
      sut.nameForSerialization(beanPropertyDefinition) == name
      sut.nameForDeserialization(beanPropertyDefinition) == name

    where:
      beanAccessorMethod     | name
      "getAnObject"          | "an_object"
      "setaByte"             | "a_byte"
      "getAnObjectBoolean"   | "an_object_boolean"
      "setDate"              | "date"
  }

  def "rename setting CamelCase strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
      ObjectMapperBeanPropertyNamingStrategy sut = new ObjectMapperBeanPropertyNamingStrategy(objectMapper);
      def beanPropertyDefinition = beanPropertyDefinition(simpleType(), beanAccessorMethod)

    expect:
      sut.nameForSerialization(beanPropertyDefinition) == name
      sut.nameForDeserialization(beanPropertyDefinition) == name

    where:
      beanAccessorMethod     | name
      "getAnObject"          | "AnObject"
      "setaByte"             | "AByte"
      "getAnObjectBoolean"   | "AnObjectBoolean"
      "setDate"              | "Date"
  }
}
