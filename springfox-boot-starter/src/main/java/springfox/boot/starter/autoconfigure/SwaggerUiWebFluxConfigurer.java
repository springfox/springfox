package springfox.boot.starter.autoconfigure;

import org.springframework.util.StringUtils;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

// tag::swagger-ui-configurer[]
public class SwaggerUiWebFluxConfigurer implements WebFluxConfigurer {
  private final String baseUrl;

  public SwaggerUiWebFluxConfigurer(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');
    registry.
        addResourceHandler(baseUrl + "/swagger-ui/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
        .resourceChain(false);
  }
}
// end::swagger-ui-configurer[]
