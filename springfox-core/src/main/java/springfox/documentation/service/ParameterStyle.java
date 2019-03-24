package springfox.documentation.service;

public enum ParameterStyle {
  MATRIX("matrix"),
  LABEL("label"),
  FORM("form"),
  SIMPLE("simple"),
  SPACE_DELIMITED("spaceDelimited"),
  PIPE_DELIMITED("pipeDelimited"),
  DEEPOBJECT("deepObject");

  private String value;

  ParameterStyle(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
