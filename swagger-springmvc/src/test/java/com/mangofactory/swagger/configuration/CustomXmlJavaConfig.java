package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
public class CustomXmlJavaConfig {
  @Autowired
  private Defaults defaults;

  @Bean
  public SwaggerSpringMvcPlugin customImplementation() {
    return new SwaggerSpringMvcPlugin(this.defaults)
            .swaggerGroup("customPlugin")
            .includePatterns(".*pet.*");
  }

  @Bean
  public SwaggerSpringMvcPlugin secondCustomImplementation() {
    return new SwaggerSpringMvcPlugin(this.defaults)
            .swaggerGroup("secondCustomPlugin")
            .includePatterns("/feature.*");
  }
}
