package springfox.documentation.swagger.dto;

public abstract class AuthorizationType {
  protected final String type;
  protected String name;

  protected AuthorizationType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
