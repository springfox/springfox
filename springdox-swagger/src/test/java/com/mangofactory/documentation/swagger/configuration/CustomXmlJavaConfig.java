package com.mangofactory.documentation.swagger.configuration;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spring.web.plugins.DocumentationConfigurer;
import com.mangofactory.documentation.swagger.annotations.EnableSwagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
