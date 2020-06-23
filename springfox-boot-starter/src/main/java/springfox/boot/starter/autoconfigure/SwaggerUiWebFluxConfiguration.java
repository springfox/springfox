package springfox.boot.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import static springfox.documentation.builders.BuilderDefaults.*;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(
    value = "springfox.documentation.swagger-ui.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SwaggerUiWebFluxConfiguration {
  @Value("${springfox.documentation.swagger-ui.base-url:}")
  private String swaggerBaseUrl;

  @Bean
  public SwaggerUiWebFluxConfigurer swaggerUiWebfluxConfigurer(SwaggerUiWebFluxTransformer transformer) {
    return new SwaggerUiWebFluxConfigurer(fixup(swaggerBaseUrl), transformer);
  }

  @Bean
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  public SwaggerUiWebFluxTransformer swaggerUiWebFluxTransformer() {
    return new SwaggerUiWebFluxTransformer(fixup(swaggerBaseUrl));
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }
}
