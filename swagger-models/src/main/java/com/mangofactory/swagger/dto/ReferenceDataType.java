package com.mangofactory.swagger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReferenceDataType implements SwaggerDataType {
  @JsonProperty("type")
  private String reference;

  public ReferenceDataType(String reference) {
    this.reference = reference;
  }

  public String getReference() {
    return reference;
  }

  @Override
  public String getAbsoluteType() {
    return reference;
  }
}
