package com.mangofactory.swagger.models

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import spock.lang.Specification

class ObjectMapperNamingStrategySpec extends Specification {

  def "rename without setting an strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectMapperNamingStrategy sut = new ObjectMapperNamingStrategy(objectMapper);

    expect:
      sut.name(currentPropertyName) == renamedPropertyName

    where:
      currentPropertyName | renamedPropertyName
      "propertyName"      | "propertyName"
      "name"              | "name"
      "propertyLongName"  | "propertyLongName"
  }

  def "rename setting snake_case strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
      ObjectMapperNamingStrategy sut = new ObjectMapperNamingStrategy(objectMapper);

    expect:
      sut.name(currentPropertyName) == renamedPropertyName

    where:
      currentPropertyName | renamedPropertyName
      "propertyName"      | "property_name"
      "name"              | "name"
      "propertyLongName"  | "property_long_name"
  }

  def "rename setting CamelCase strategy"() {
    given:
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
      ObjectMapperNamingStrategy sut = new ObjectMapperNamingStrategy(objectMapper);

    expect:
      sut.name(currentPropertyName) == renamedPropertyName

    where:
      currentPropertyName | renamedPropertyName
      "propertyName"      | "PropertyName"
      "name"              | "Name"
      "propertyLongName"  | "PropertyLongName"
  }
}
