package com.mangofactory.swagger.models.dto;

public class ModelRef {

  private final String type;
  private final String ref;
  private final String qualifiedType;

  public ModelRef(String type, String ref, String qualifiedType) {
    this.type = type;
    this.ref = ref;
    this.qualifiedType = qualifiedType;
  }

  public String getType() {
    return type;
  }

  public String getRef() {
    return ref;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }
}
