package com.mangofactory.swagger.models;

import java.util.HashSet;
import java.util.Set;

public class SetsContainer {
  private Set<ComplexType> complexTypes;
  private Set<ExampleEnum> enums;
  private Set<Integer> integers;
  private HashSet<String> strings;
  private Set<Object> objects;

  SetsContainer(Set<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public Set<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(Set<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public Set<ExampleEnum> getEnums() {
    return enums;
  }

  public void setEnums(Set<ExampleEnum> enums) {
    this.enums = enums;
  }

  public Set<Integer> getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(Set<Integer> years) {
    this.integers = years;
  }

  public HashSet<String> getStrings() {
    return strings;
  }

  public void setStrings(HashSet<String> strings) {
    this.strings = strings;
  }

  public Set<Object> getObjects() {
    return objects;
  }

  public void setObjects(Set<Object> objects) {
    this.objects = objects;
  }
}
