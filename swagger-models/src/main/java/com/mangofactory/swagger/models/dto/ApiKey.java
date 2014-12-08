package com.mangofactory.swagger.models.dto;

public class ApiKey extends AuthorizationType {
  private final String keyname;
  private final String passAs;

  public ApiKey(String keyname, String passAs) {
    super("apiKey");
    this.keyname = keyname;
    this.passAs = passAs;
  }

  public ApiKey(String keyname) {
    super("apiKey");
    this.keyname = keyname;
    this.passAs = "header";
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
