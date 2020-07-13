package springfox.documentation.swagger2.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springfox.documentation.spring.web.OnReactiveWebApplication;
import springfox.documentation.swagger2.web.WebFluxBasePathAndHostnameTransformationFilter;
import springfox.documentation.swagger2.web.WebFluxSwaggerTransformationFilter;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Conditional(OnReactiveWebApplication.class)
@EnablePluginRegistries(WebFluxSwaggerTransformationFilter.class)
public class Swagger2WebFluxConfiguration {
  @Bean
  public WebFluxSwaggerTransformationFilter webMvcSwaggerTransformer() {
    return new WebFluxBasePathAndHostnameTransformationFilter();
  }
}
