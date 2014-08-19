package com.mangofactory.swagger.models;

import java.util.ArrayList;
import java.util.List;

public class ListsContainer {
  private List<ComplexType> complexTypes;
  private List<ExampleEnum> enums;
  private List<Integer> integers;
  private ArrayList<String> strings;
  private List<Object> objects;

  ListsContainer(List<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public List<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  public void setComplexTypes(List<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
  }

  public List<ExampleEnum> getEnums() {
    return enums;
  }

  public void setEnums(List<ExampleEnum> enums) {
    this.enums = enums;
  }

  public List<Integer> getAliasOfIntegers() {
    return integers;
  }

  public void setAliasOfIntegers(List<Integer> years) {
    this.integers = years;
  }

  public ArrayList<String> getStrings() {
    return strings;
  }

  public void setStrings(ArrayList<String> strings) {
    this.strings = strings;
  }

  public List<Object> getObjects() {
    return objects;
  }

  public void setObjects(List<Object> objects) {
    this.objects = objects;
  }
}
