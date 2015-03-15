package springdox.documentation.swagger.dto;

public class TokenRequestEndpoint {

  private String url;
  private String clientIdName;
  private String clientSecretName;

  public TokenRequestEndpoint() {
  }

  public TokenRequestEndpoint(String url, String clientIdName, String clientSecretName) {
    this.url = url;
    this.clientIdName = clientIdName;
    this.clientSecretName = clientSecretName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getClientIdName() {
    return clientIdName;
  }

  public void setClientIdName(String clientIdName) {
    this.clientIdName = clientIdName;
  }

  public String getClientSecretName() {
    return clientSecretName;
  }

  public void setClientSecretName(String clientSecretName) {
    this.clientSecretName = clientSecretName;
  }
}
