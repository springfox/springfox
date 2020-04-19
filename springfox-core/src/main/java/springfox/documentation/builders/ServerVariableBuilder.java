package springfox.documentation.builders;

import springfox.documentation.service.ServerVariable;
import springfox.documentation.service.VendorExtension;

import java.util.List;

public class ServerVariableBuilder {
  private String name;
  private List<String> allowedValues;
  private String defaultValue;
  private String description;
  private List<VendorExtension> extensions;

  public ServerVariableBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ServerVariableBuilder allowedValues(List<String> allowedValues) {
    this.allowedValues = allowedValues;
    return this;
  }

  public ServerVariableBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public ServerVariableBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ServerVariableBuilder extensions(List<VendorExtension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public ServerVariable createServerVariable() {
    return new ServerVariable(name, allowedValues, defaultValue, description, extensions);
  }
}