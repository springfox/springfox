package springfox.documentation.oas.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springfox.documentation.oas.web.WebMvcBasePathAndHostnameTransformationFilter;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spring.web.OnServletBasedWebApplication;

import static springfox.documentation.oas.web.SpecGeneration.*;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Conditional(OnServletBasedWebApplication.class)
@EnablePluginRegistries(WebMvcOpenApiTransformationFilter.class)
public class OpenApiWebMvcConfiguration {
  @Bean
  public WebMvcOpenApiTransformationFilter webMvcOpenApiTransformer(
      @Value(OPEN_API_SPECIFICATION_PATH) String oasPath) {
    return new WebMvcBasePathAndHostnameTransformationFilter(oasPath);
  }
}
