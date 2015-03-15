package springdox.documentation.service;

public abstract class AuthorizationType {
  protected final String name;
  protected final String type;

  protected AuthorizationType(String name, String type) {
    this.type = type;
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
