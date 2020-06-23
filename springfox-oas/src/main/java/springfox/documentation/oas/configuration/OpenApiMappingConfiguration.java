package springfox.documentation.oas.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;

@Configuration
public class OpenApiMappingConfiguration {
  @Bean
  public JacksonModuleRegistrar openApiModule() {
    return new OpenApiJacksonModule();
  }
}
