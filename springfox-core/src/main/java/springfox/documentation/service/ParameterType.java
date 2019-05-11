package springfox.documentation.service;

public enum ParameterType {
  QUERY("query"),
  HEADER("header"),
  PATH("path"),
  COOKIE("cookie"),
  FORM("form"),
  @Deprecated
  FORMDATA("formData"),
  @Deprecated
  BODY("body");

  private String in;

  ParameterType(String in) {
    this.in = in;
  }

  public String getIn() {
    return in;
  }
}
