package springdox.documentation.swagger.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spring.web.plugins.Docket;
import springdox.documentation.swagger.annotations.EnableSwagger;

import static springdox.documentation.builders.PathSelectors.*;

@Configuration
@EnableSwagger
public class CustomXmlJavaConfig {
  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("customPlugin")
        .select()
          .paths(regex(".*pet.*"))
          .build();
  }

  @Bean
  public Docket secondCustomImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("secondCustomPlugin")
        .select()
          .paths(regex("/feature.*"))
          .build();
  }
}
