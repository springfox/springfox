package springfox.documentation.schema;

public class RecursiveType {
  private RecursiveType parent;
  private SimpleType simpleProperties;

  public RecursiveType getParent() {
    return parent;
  }

  public void setParent(RecursiveType parent) {
    this.parent = parent;
  }

  public SimpleType getSimpleProperties() {
    return simpleProperties;
  }

  public void setSimpleProperties(SimpleType simpleProperties) {
    this.simpleProperties = simpleProperties;
  }
}
