package springfox.documentation.service;

import java.util.Arrays;
import java.util.Objects;

public enum ParameterType {
  QUERY("query"),
  HEADER("header"),
  PATH("path"),
  COOKIE("cookie"),
  FORM("form"),
  FORMDATA("formData"),
  BODY("body");

  private String in;

  ParameterType(String in) {
    this.in = in;
  }

  public String getIn() {
    return in;
  }

  public static ParameterType from(String value) {
    return Arrays.stream(ParameterType.values())
        .filter(each -> Objects.equals(each.in, value))
        .findFirst()
        .orElse(null);
  }
}
