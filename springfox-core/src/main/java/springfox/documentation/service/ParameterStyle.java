package springfox.documentation.service;

public enum ParameterStyle {
  MATRIX("matrix"),
  LABEL("label"),
  FORM("form"),
  SIMPLE("simple"),
  SPACEDELIMITED("spaceDelimited"),
  PIPEDELIMITED("pipeDelimited"),
  DEEPOBJECT("deepObject"),
  DEFAULT("");

  private String value;

  ParameterStyle(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
