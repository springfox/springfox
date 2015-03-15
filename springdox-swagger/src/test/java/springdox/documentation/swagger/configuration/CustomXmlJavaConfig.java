package springdox.documentation.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spring.web.plugins.DocumentationConfigurer;
import springdox.documentation.swagger.annotations.EnableSwagger;

@Configuration
@EnableSwagger
public class CustomXmlJavaConfig {
  @Bean
  public DocumentationConfigurer customImplementation() {
    return new DocumentationConfigurer(DocumentationType.SWAGGER_12)
            .groupName("customPlugin")
            .includePatterns(".*pet.*");
  }

  @Bean
  public DocumentationConfigurer secondCustomImplementation() {
    return new DocumentationConfigurer(DocumentationType.SWAGGER_12)
            .groupName("secondCustomPlugin")
            .includePatterns("/feature.*");
  }
}
