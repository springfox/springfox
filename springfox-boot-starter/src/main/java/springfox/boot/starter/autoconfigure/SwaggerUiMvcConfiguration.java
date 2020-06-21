package springfox.boot.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import static springfox.documentation.builders.BuilderDefaults.*;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SwaggerUiMvcConfiguration {
  @Value("${springfox.documentation.resources.baseUrl:}")
  private String swaggerBaseUrl;

  @Bean
  public SwaggerUiConfigurer swaggerUiConfigurer(SwaggerUiTransformer transformer) {
    return new SwaggerUiConfigurer(fixup(swaggerBaseUrl), transformer);
  }

  @Bean
  public SwaggerUiTransformer swaggerUiTransformer() {
    return new SwaggerUiTransformer(fixup(swaggerBaseUrl));
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }
}
