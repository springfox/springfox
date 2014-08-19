package com.mangofactory.swagger.models;

import com.wordnik.swagger.annotations.ApiModelProperty;

public class TypeWithAnnotatedGettersAndSetters {
  @ApiModelProperty(notes = "int Property Field", required = true)
  private int intProp;
  private boolean boolProp;
  private ExampleEnum enumProp;
  private GenericType<String> genericProp;

  public int getIntProp() {
    return intProp;
  }

  public void setIntProp(int intProp) {
    this.intProp = intProp;
  }

  @ApiModelProperty(notes = "bool Property Getter", required = false)
  public boolean isBoolProp() {
    return boolProp;
  }

  public void setBoolProp(boolean boolProp) {
    this.boolProp = boolProp;
  }

  public void getVoid() {
  }

  public int isNotGetter() {
    return 0;
  }

  public int getWithParam(int param) {
    return 0;
  }

  public int setNotASetter() {
    return 0;
  }

  @ApiModelProperty(value = "enum Prop Getter value", notes = "enum note", allowableValues = "ONE", required = true)
  public ExampleEnum getEnumProp() {
    return enumProp;
  }

  public void setEnumProp(ExampleEnum enumProp) {
    this.enumProp = enumProp;
  }
}
