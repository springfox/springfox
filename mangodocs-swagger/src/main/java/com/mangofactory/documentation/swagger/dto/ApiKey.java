package com.mangofactory.documentation.swagger.dto;

public class ApiKey extends AuthorizationType {
  private String keyname;
  private String passAs;

  public ApiKey() {
    super("apiKey");
  }

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

  public String getKeyname() {
    return keyname;
  }

  public void setKeyname(String keyname) {
    this.keyname = keyname;
  }

  public String getPassAs() {
    return passAs;
  }

  public void setPassAs(String passAs) {
    this.passAs = passAs;
  }
}
