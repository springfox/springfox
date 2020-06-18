package springfox.boot.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

@Configuration
@Import({
    OpenApiDocumentationConfiguration.class,
    SpringDataRestConfiguration.class,
    BeanValidatorPluginsConfiguration.class,
    Swagger2DocumentationConfiguration.class
})
public class OpenApiAutoConfiguration {
  @Value("${springfox.documentation.resources.baseUrl:/}")
  private String swaggerBaseUrl;

  @Bean
  public SwaggerUiConfigurer swaggerUiConfigurer(SwaggerUiTransformer transformer) {
    return new SwaggerUiConfigurer(swaggerBaseUrl, transformer);
  }

  @Bean
  public SwaggerUiTransformer swaggerUiTransformer() {
    return new SwaggerUiTransformer(swaggerBaseUrl);
  }
}
