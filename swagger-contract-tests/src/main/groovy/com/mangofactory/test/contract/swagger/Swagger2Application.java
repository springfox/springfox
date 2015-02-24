package com.mangofactory.test.contract.swagger;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spring.web.plugins.DocumentationConfigurer;
import com.mangofactory.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2
@ComponentScan({

        "com.mangofactory.documentation.spring.web.dummy.controllers",
        "com.mangofactory.test.contract.swagger",
        "com.mangofactory.petstore.controller"
})
public class Swagger2Application {


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
    SpringApplication.run(Swagger2Application.class, args);
  }
}
