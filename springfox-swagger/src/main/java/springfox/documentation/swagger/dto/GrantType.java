package springfox.documentation.swagger.dto;

public class GrantType {
  private String type;

  public GrantType() {
  }

  public GrantType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
