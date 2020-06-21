package springfox.boot.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.oas.configuration.OpenApiDocumentationConfiguration;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

import static springfox.documentation.builders.BuilderDefaults.*;

@Configuration
@Import({
    OpenApiDocumentationConfiguration.class,
    SpringDataRestConfiguration.class,
    BeanValidatorPluginsConfiguration.class,
    Swagger2DocumentationConfiguration.class
})
public class OpenApiAutoConfiguration {
  @Value("${springfox.documentation.resources.baseUrl:}")
  private String swaggerBaseUrl;

  @Bean
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  public SwaggerUiWebFluxConfigurer swaggerUiWebfluxConfigurer(SwaggerUiWebFluxTransformer transformer) {
    return new SwaggerUiWebFluxConfigurer(fixup(swaggerBaseUrl), transformer);
  }

  @Bean
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  public SwaggerUiWebFluxTransformer swaggerUiWebFluxTransformer() {
    return new SwaggerUiWebFluxTransformer(fixup(swaggerBaseUrl));
  }

  @Bean
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  public SwaggerUiWebMvcConfigurer swaggerUiConfigurer(SwaggerUiWebMvcTransformer transformer) {
    return new SwaggerUiWebMvcConfigurer(fixup(swaggerBaseUrl), transformer);
  }

  @Bean
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  public SwaggerUiWebMvcTransformer swaggerUiTransformer() {
    return new SwaggerUiWebMvcTransformer(fixup(swaggerBaseUrl));
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }
}
