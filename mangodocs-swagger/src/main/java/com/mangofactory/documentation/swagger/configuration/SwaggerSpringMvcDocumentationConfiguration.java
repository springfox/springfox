package com.mangofactory.documentation.swagger.configuration;

import com.mangofactory.documentation.spring.web.SpringMvcDocumentationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SpringMvcDocumentationConfiguration.class })
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.swagger.schema",
        "com.mangofactory.documentation.swagger.web",
        "com.mangofactory.documentation.swagger.readers.operation",
        "com.mangofactory.documentation.swagger.readers.parameter",
        "com.mangofactory.documentation.swagger.dto.mappers"
})
public class SwaggerSpringMvcDocumentationConfiguration {

  /**
   * Registers some custom serializers needed to transform swagger models to swagger-ui required json format.
   */
  @Bean
  public JacksonSwaggerSupport jacksonSwaggerSupport() {
    return new JacksonSwaggerSupport();
  }

}
