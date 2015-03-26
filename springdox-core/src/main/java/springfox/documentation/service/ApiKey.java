package springfox.documentation.service;

public class ApiKey extends AuthorizationType {
  private final String keyname;
  private final String passAs;

  public ApiKey(String name, String keyname, String passAs) {
    super(name, "apiKey");
    this.keyname = keyname;
    this.passAs = passAs;
  }

  public String getKeyname() {
    return keyname;
  }

  public String getPassAs() {
    return passAs;
  }
}
