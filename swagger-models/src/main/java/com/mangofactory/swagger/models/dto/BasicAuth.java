package com.mangofactory.swagger.models.dto;

public class BasicAuth extends AuthorizationType {
  public BasicAuth() {
    super("basicAuth");
  }

  @Override
  public String getName() {
    return super.type;
  }
}
