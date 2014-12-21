package com.mangofactory.service.model;

public class ReferenceDataType implements SwaggerDataType {
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
