package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class TypeForTestingPropertyNames {
  public int getProp() {
    throw new UnsupportedOperationException();
  }
  public int getProp1() {
    throw new UnsupportedOperationException();
  }
  public int getProp_1() {
    throw new UnsupportedOperationException();
  }
  public int isProp() {
    throw new UnsupportedOperationException();
  }
  public int isProp1() {
    throw new UnsupportedOperationException();
  }
  public int isProp_1() {
    throw new UnsupportedOperationException();
  }
  public void setProp(int a) {
    throw new UnsupportedOperationException();
  }
  public void setProp1(int a) {
    throw new UnsupportedOperationException();
  }
  public void setProp_1(int a) {
    throw new UnsupportedOperationException();
  }
  public int prop() {
    throw new UnsupportedOperationException();
  }
  @JsonGetter("prop")
  public int getAnotherProp() {
    throw new UnsupportedOperationException();
  }
  @JsonSetter("prop")
  public void setAnotherProp(int a) {
    throw new UnsupportedOperationException();
  }
  @JsonGetter
  public int getPropFallback() {
    throw new UnsupportedOperationException();
  }
  @JsonSetter
  public void setPropFallback(int a) {
    throw new UnsupportedOperationException();
  }
}
