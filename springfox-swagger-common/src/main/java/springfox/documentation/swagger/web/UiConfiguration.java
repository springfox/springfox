package springfox.documentation.swagger.web;

public class UiConfiguration {
  public static final UiConfiguration DEFAULT = new UiConfiguration(null);
  private String validatorUrl;

  public UiConfiguration(String validatorUrl) {
    this.validatorUrl = validatorUrl;
  }

  public String getValidatorUrl() {
    return validatorUrl;
  }
}
