package springfox.documentation.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.annotations.EnableSwagger;
import springfox.documentation.builders.PathSelectors;

@Configuration
@EnableSwagger
public class CustomXmlJavaConfig {
  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("customPlugin")
        .select()
          .paths(PathSelectors.regex(".*pet.*"))
          .build();
  }

  @Bean
  public Docket secondCustomImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("secondCustomPlugin")
        .select()
          .paths(PathSelectors.regex("/feature.*"))
          .build();
  }
}
