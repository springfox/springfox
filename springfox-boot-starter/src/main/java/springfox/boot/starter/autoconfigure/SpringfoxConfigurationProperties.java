package springfox.boot.starter.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("springfox.documentation")
public class SpringfoxConfigurationProperties {
  private boolean enabled = true;
  @NestedConfigurationProperty
  private SwaggerConfigurationProperties swagger;
  @NestedConfigurationProperty
  private OpenApiConfigurationProperties openApi;
  @NestedConfigurationProperty
  private SwaggerUiConfigurationProperties swaggerUi;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public SwaggerConfigurationProperties getSwagger() {
    return swagger;
  }

  public void setSwagger(SwaggerConfigurationProperties swagger) {
    this.swagger = swagger;
  }

  public OpenApiConfigurationProperties getOpenApi() {
    return openApi;
  }

  public void setOpenApi(OpenApiConfigurationProperties openApi) {
    this.openApi = openApi;
  }

  public SwaggerUiConfigurationProperties getSwaggerUi() {
    return swaggerUi;
  }

  public void setSwaggerUi(SwaggerUiConfigurationProperties swaggerUi) {
    this.swaggerUi = swaggerUi;
  }

  public static class SwaggerConfigurationProperties {
    @NestedConfigurationProperty
    private Swagger2Configuration v2;

    public Swagger2Configuration getV2() {
      return v2;
    }

    public void setV2(Swagger2Configuration v2) {
      this.v2 = v2;
    }

  }

  public static class Swagger2Configuration {
    private boolean enabled = false;
    private boolean useModelV3 = true;
    private String host;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public boolean isUseModelV3() {
      return useModelV3;
    }

    public void setUseModelV3(boolean useModelV3) {
      this.useModelV3 = useModelV3;
    }
  }

  public static class OpenApiConfigurationProperties {
    private boolean enabled = true;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class SwaggerUiConfigurationProperties {
    private boolean enabled = true;
    private String baseUrl = "";

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }
  }
}
