package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSwagger
public class CustomXmlJavaConfig {

  private SpringSwaggerConfig springSwaggerConfig;

  /**
   * Required to autowire SpringSwaggerConfig
   *
   * @param springSwaggerConfig
   */
  @Autowired
  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

  @Bean
  public SwaggerSpringMvcPlugin customImplementation() {
    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
            .swaggerGroup("customPlugin")
            .includePatterns(".*pet.*");
  }

  @Bean
  public SwaggerSpringMvcPlugin secondCustomImplementation() {
    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
            .swaggerGroup("secondCustomPlugin")
            .includePatterns("/feature.*");
  }
}
