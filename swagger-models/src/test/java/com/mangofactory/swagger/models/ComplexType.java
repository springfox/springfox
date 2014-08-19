package com.mangofactory.swagger.models;

import java.math.BigDecimal;

public class ComplexType {
  String name;
  int age;
  Category category;
  BigDecimal customType;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public BigDecimal getCustomType() {
    return customType;
  }

  public void setCustomType(BigDecimal customType) {
    this.customType = customType;
  }
}
