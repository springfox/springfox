package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReferenceDataType implements SwaggerDataType {
  @JsonProperty("type")
  private final String reference;

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
