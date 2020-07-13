package springfox.documentation.oas.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springfox.documentation.oas.web.WebFluxBasePathAndHostnameTransformationFilter;
import springfox.documentation.oas.web.WebFluxOpenApiTransformationFilter;
import springfox.documentation.spring.web.OnReactiveWebApplication;

import static springfox.documentation.oas.web.SpecGeneration.*;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Conditional(OnReactiveWebApplication.class)
@EnablePluginRegistries(WebFluxOpenApiTransformationFilter.class)
public class OpenApiWebFluxConfiguration {
  @Bean
  public WebFluxOpenApiTransformationFilter webMvcOpenApiTransformer(
      @Value(OPEN_API_SPECIFICATION_PATH) String oasPath) {
    return new WebFluxBasePathAndHostnameTransformationFilter(oasPath);
  }
}
