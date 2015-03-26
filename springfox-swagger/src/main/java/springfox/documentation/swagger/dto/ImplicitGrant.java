package springfox.documentation.swagger.dto;


public class ImplicitGrant extends GrantType {
  private LoginEndpoint loginEndpoint;
  private String tokenName;

  public ImplicitGrant() {
    super("implicit");
  }

  public ImplicitGrant(LoginEndpoint loginEndpoint, String tokenName) {
    super("implicit");
    this.loginEndpoint = loginEndpoint;
    this.tokenName = tokenName;
  }

  public LoginEndpoint getLoginEndpoint() {
    return loginEndpoint;
  }

  public void setLoginEndpoint(LoginEndpoint loginEndpoint) {
    this.loginEndpoint = loginEndpoint;
  }

  public String getTokenName() {
    return tokenName;
  }

  public void setTokenName(String tokenName) {
    this.tokenName = tokenName;
  }
}
