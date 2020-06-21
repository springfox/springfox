package springfox.boot.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static springfox.documentation.builders.BuilderDefaults.*;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class SwaggerUiWebFluxConfiguration {
  @Value("${springfox.documentation.resources.baseUrl:}")
  private String swaggerBaseUrl;
  
  @Bean
  public SwaggerUiWebFluxConfigurer swaggerUiWebfluxConfigurer(SwaggerUiWebFluxTransformer transformer) {
    return new SwaggerUiWebFluxConfigurer(fixup(swaggerBaseUrl), transformer);
  }

  @Bean
  public SwaggerUiWebFluxTransformer swaggerUiWebFluxTransformer() {
    return new SwaggerUiWebFluxTransformer(fixup(swaggerBaseUrl));
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }
}
