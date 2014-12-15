package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelRef {
  private final String type;
  @JsonProperty("$ref")
  private final String ref;
  @JsonIgnore
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
