package springfox.documentation.swagger2.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springfox.documentation.spring.web.OnServletBasedWebApplication;
import springfox.documentation.swagger2.web.WebMvcBasePathAndHostnameTransformationFilter;
import springfox.documentation.swagger2.web.WebMvcSwaggerTransformationFilter;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Conditional(OnServletBasedWebApplication.class)
@EnablePluginRegistries(WebMvcSwaggerTransformationFilter.class)
public class Swagger2WebMvcConfiguration {
  @Bean
  public WebMvcSwaggerTransformationFilter webMvcSwaggerTransformer(Environment env) {
    return new WebMvcBasePathAndHostnameTransformationFilter(env);
  }
}
