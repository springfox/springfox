package springfox.boot.starter.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static springfox.documentation.builders.BuilderDefaults.*;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    value = "springfox.documentation.swagger-ui.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SwaggerUiWebMvcConfiguration {

  @Bean
  public SwaggerUiWebMvcConfigurer swaggerUiConfigurer(SpringfoxConfigurationProperties properties) {
    return new SwaggerUiWebMvcConfigurer(fixup(properties.getSwaggerUi().getBaseUrl()));
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }
}
