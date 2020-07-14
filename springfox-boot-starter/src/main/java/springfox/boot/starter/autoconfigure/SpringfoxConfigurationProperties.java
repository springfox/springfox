package springfox.boot.starter.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("springfox.documentation")
public class SpringfoxConfigurationProperties {
  /**
   * Flag that indicates if springfox should auto start scanning services
   */
  private boolean autoStartup = true;
  /**
   * Flag that indicates if springfox should scan services to produce specifications.
   */
  private boolean enabled = true;
  @NestedConfigurationProperty
  private SwaggerConfigurationProperties swagger;
  @NestedConfigurationProperty
  private OpenApiConfigurationProperties openApi;
  @NestedConfigurationProperty
  private SwaggerUiConfigurationProperties swaggerUi;

  public boolean isAutoStartup() {
    return autoStartup;
  }

  public void setAutoStartup(boolean autoStartup) {
    this.autoStartup = autoStartup;
  }

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

  /**
   * Configuration properties related to swagger specification.
   *
   * @since 3.0.0
   */
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

  /**
   * Configuration properties related to swagger 2.0 specification.
   *
   * @since 3.0.0
   */
  public static class Swagger2Configuration {
    /**
     * Flag that disables swagger 2 related beans
     */
    private boolean enabled = false;
    /**
     * Flag that toggles between generating models using v3.0.0 of the library or the 2.9.x version of the library.
     * This flag can be changed to false, if v3.0.0 is not working as expected.
     */
    private boolean useModelV3 = true;
    /**
     * Host name to use for swagger 2 specification
     */
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
  /**
   * Configuration properties related to OpenApi 3.0 specification.
   *
   * @since 3.0.0
   */
  public static class OpenApiConfigurationProperties {
    /**
     * Flag that disables openApi related beans
     */
    private boolean enabled = true;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  /**
   * Configuration properties related to swagger-ui specification.
   *
   * @since 3.0.0
   */
  public static class SwaggerUiConfigurationProperties {
    /**
     * Flag that disables swagger-ui from being resource-handled. No swagger-ui when this is false.
     */
    private boolean enabled = true;
    /**
     * Base url for swagger-ui. For e.g. setting it to /documentation will put swagger-ui
     * at /documentation/swagger-ui/index.html
     */
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
