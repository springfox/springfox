package com.mangofactory.documentation.service;

public class ApiKey extends AuthorizationType {
  private final String keyname;
  private final String passAs;

  public ApiKey(String keyname, String passAs) {
    super("apiKey");
    this.keyname = keyname;
    this.passAs = passAs;
  }

  @Override
  public String getName() {
    return keyname;
  }

  public String getKeyname() {
    return keyname;
  }

  public String getPassAs() {
    return passAs;
  }
}
