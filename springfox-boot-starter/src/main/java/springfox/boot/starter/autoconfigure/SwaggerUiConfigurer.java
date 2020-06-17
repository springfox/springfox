package springfox.boot.starter.autoconfigure;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class SwaggerUiConfigurer implements WebMvcConfigurer {

  private final String baseUrl;
  private final SwaggerUiTransformer transformer;

  public SwaggerUiConfigurer(
      String baseUrl,
      SwaggerUiTransformer transformer) {
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
