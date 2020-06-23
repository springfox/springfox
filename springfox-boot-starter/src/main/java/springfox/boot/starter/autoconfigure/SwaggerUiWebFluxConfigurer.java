package springfox.boot.starter.autoconfigure;

import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.resource.ResourceTransformer;

public class SwaggerUiWebFluxConfigurer implements WebFluxConfigurer {
  private final String baseUrl;
  private final ResourceTransformer transformer;

  public SwaggerUiWebFluxConfigurer(
      String baseUrl,
      ResourceTransformer transformer) {
    this.baseUrl = baseUrl;
    this.transformer = transformer;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.
        addResourceHandler(baseUrl + "/swagger-ui.html**")
        .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html")
        .resourceChain(false)
        .addTransformer(transformer);
    registry.
        addResourceHandler(baseUrl + "/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/")
        .resourceChain(false)
        .addTransformer(transformer);
  }
}
