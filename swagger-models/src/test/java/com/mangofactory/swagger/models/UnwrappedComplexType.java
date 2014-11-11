package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class UnwrappedComplexType {
  private String value;
  private Category category;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @JsonUnwrapped
  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }
}
