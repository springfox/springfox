package springfox.documentation.service;

public enum ParameterType {
  QUERY("query"),
  HEADER("header"),
  PATH("path"),
  COOKIE("cookie");

  private String in;

  ParameterType(String in) {
    this.in = in;
  }

  public String getIn() {
    return in;
  }
}
