package com.mangofactory.swagger.models;

import java.util.Map;

public class MapsContainer {
  private Map<ExampleEnum, SimpleType> enumToSimpleType;
  private Map<String, SimpleType> stringToSimpleType;
  private Map<Category, SimpleType> complexToSimpleType;

  public Map<ExampleEnum, SimpleType> getEnumToSimpleType() {
    return enumToSimpleType;
  }

  public void setEnumToSimpleType(Map<ExampleEnum, SimpleType> enumToSimpleType) {
    this.enumToSimpleType = enumToSimpleType;
  }

  public Map<String, SimpleType> getStringToSimpleType() {
    return stringToSimpleType;
  }

  public void setStringToSimpleType(Map<String, SimpleType> stringToSimpleType) {
    this.stringToSimpleType = stringToSimpleType;
  }

  public Map<Category, SimpleType> getComplexToSimpleType() {
    return complexToSimpleType;
  }

  public void setComplexToSimpleType(Map<Category, SimpleType> complexToSimpleType) {
    this.complexToSimpleType = complexToSimpleType;
  }
}
