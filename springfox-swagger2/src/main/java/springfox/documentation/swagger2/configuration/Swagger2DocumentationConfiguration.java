package springfox.documentation.swagger2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;

@Configuration
@Import({
    Swagger2DocumentationWebFluxConfiguration.class,
    Swagger2DocumentationWebMvcConfiguration.class,
    SwaggerCommonConfiguration.class,
    SpringfoxWebConfiguration.class,
})
@ComponentScan(basePackages = {
    "springfox.documentation.swagger2.mappers"
})
public class Swagger2DocumentationConfiguration {
  @Bean
  public JacksonModuleRegistrar swagger2Module() {
    return new Swagger2JacksonModule();
  }
}
