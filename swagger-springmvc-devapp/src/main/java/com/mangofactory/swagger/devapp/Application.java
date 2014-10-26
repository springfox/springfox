package com.mangofactory.swagger.devapp;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableSwagger
public class Application {
  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(Application.class, args);
  }

  private SpringSwaggerConfig springSwaggerConfig;

  @Autowired
  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

//  @Bean
//  public SwaggerSpringMvcPlugin customImplementation(){
//    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
//            .swaggerGroup("user-group")
//            .includePatterns(".*users.*");
//  }

}
