package com.mangofactory.swagger.models.servicemodel;

public class BasicAuth extends AuthorizationType {
  public BasicAuth() {
    super("basicAuth");
  }

  @Override
  public String getName() {
    return super.type;
  }
}
