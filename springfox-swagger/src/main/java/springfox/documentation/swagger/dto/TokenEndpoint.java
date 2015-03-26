package springfox.documentation.swagger.dto;

public class TokenEndpoint {

  private String url;
  private String tokenName;

  public TokenEndpoint() {
  }

  public TokenEndpoint(String url, String tokenName) {
    this.url = url;
    this.tokenName = tokenName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTokenName() {
    return tokenName;
  }

  public void setTokenName(String tokenName) {
    this.tokenName = tokenName;
  }
}
