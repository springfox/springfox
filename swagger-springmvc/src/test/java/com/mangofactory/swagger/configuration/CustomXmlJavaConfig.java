package com.mangofactory.swagger.configuration;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.plugins.DocumentationConfigurer;
import com.mangofactory.swagger.annotations.EnableSwagger;
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
