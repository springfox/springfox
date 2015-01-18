package com.mangofactory.documentation.swagger.dto;

public class BasicAuth extends AuthorizationType {
  public BasicAuth() {
    super("basicAuth");
  }

  @Override
  public String getName() {
    return super.type;
  }
}
