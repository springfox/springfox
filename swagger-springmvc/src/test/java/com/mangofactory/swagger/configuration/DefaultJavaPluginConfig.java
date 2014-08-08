package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.mangofactory.swagger.dummy")
public class DefaultJavaPluginConfig {
  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

}
