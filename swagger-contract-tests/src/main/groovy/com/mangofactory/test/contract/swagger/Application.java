package com.mangofactory.test.contract.swagger;

import com.mangofactory.documentation.swagger.annotations.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableSwagger
@ComponentScan({"com.mangofactory.documentation.spring.web.dummy.controllers", "com.mangofactory.test.contract.swagger"})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
