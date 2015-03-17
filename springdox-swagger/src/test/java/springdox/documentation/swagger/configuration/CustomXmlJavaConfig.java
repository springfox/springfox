package springdox.documentation.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spring.web.plugins.Docket;
import springdox.documentation.swagger.annotations.EnableSwagger;

@Configuration
@EnableSwagger
public class CustomXmlJavaConfig {
  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
            .groupName("customPlugin")
            .includePatterns(".*pet.*");
  }

  @Bean
  public Docket secondCustomImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
            .groupName("secondCustomPlugin")
            .includePatterns("/feature.*");
  }
}
