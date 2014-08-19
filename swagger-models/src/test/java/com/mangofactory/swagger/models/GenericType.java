package com.mangofactory.swagger.models;

import java.util.List;

public class GenericType<T> {
  private T genericField;
  private List<String> strings;

  public T getGenericField() {
    return genericField;
  }

  public void setGenericField(T genericField) {
    this.genericField = genericField;
  }

  public List<String> getStrings() {
    return strings;
  }

  public void setStrings(List<String> strings) {
    this.strings = strings;
  }
}
