package com.mangofactory.test.contract.swagger;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spring.web.plugins.DocumentationConfigurer;
import com.mangofactory.documentation.swagger.annotations.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableSwagger
@ComponentScan({

        "com.mangofactory.documentation.spring.web.dummy.controllers",
        "com.mangofactory.test.contract.swagger",
        "com.mangofactory.petstore.controller"
})
public class SwaggerApplication {


  @Bean
  public DocumentationConfigurer testCases() {
    return new DocumentationConfigurer(DocumentationType.SWAGGER_12)
            .groupName("default")
            .includePatterns("^((?!\\/api).)*$"); //Not beginning with /api
  }

  @Bean
  public DocumentationConfigurer petstore() {
    return new DocumentationConfigurer(DocumentationType.SWAGGER_12)
            .groupName("petstore")
            .includePatterns("/api/.*");
  }
  public static void main(String[] args) {
    SpringApplication.run(SwaggerApplication.class, args);
  }
}
