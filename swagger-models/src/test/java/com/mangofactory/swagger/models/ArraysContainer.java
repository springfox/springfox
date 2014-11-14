package com.mangofactory.swagger.models;

public class ArraysContainer {
  private ComplexType[] complexTypes;
  private ExampleEnum[] enums;
  private Integer[] integers;
  private String[] strings;
  private Object[] objects;
  private byte[] bytes;

  ArraysContainer(ComplexType[] complexTypes) {
    this.complexTypes = complexTypes;
  }

  public ComplexType[] getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(ComplexType[] complexTypes) {
    this.complexTypes = complexTypes;
  }

  public ExampleEnum[] getEnums() {
    return enums;
  }

  public void setEnums(ExampleEnum[] enums) {
    this.enums = enums;
  }

  public Integer[] getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(Integer[] years) {
    this.integers = years;
  }

  public String[] getStrings() {
    return strings;
  }

  public void setStrings(String[] strings) {
    this.strings = strings;
  }

  public Object[] getObjects() {
    return objects;
  }

  public void setObjects(Object[] objects) {
    this.objects = objects;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
}

