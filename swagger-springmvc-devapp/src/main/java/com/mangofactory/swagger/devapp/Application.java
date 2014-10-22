package com.mangofactory.swagger.devapp;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableSwagger
public class Application {
  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(Application.class, args);
  }
}
