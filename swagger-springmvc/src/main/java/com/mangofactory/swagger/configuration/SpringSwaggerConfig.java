package com.mangofactory.swagger.configuration;

import com.mangofactory.schema.configuration.SwaggerModelsConfiguration;
import com.mangofactory.springmvc.plugin.DocumentationPlugin;
import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.core.SwaggerCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@ComponentScan(basePackages = {
        "com.mangofactory.swagger.controllers",
        "com.mangofactory.swagger.scanners",
        "com.mangofactory.swagger.core",
        "com.mangofactory.swagger.readers",
        "com.mangofactory.springmvc.plugin",
        "com.mangofactory.swagger.plugin"
})
@Import({ SwaggerModelsConfiguration.class, Defaults.class })
@EnablePluginRegistries({DocumentationPlugin.class})
public class SpringSwaggerConfig {


  @Bean
  public SwaggerCache swaggerCache() {
    return new SwaggerCache();
  }

  /**
   * Registers some custom serializers needed to transform swagger models to swagger-ui required json format.
   */
  @Bean
  public JacksonSwaggerSupport jacksonSwaggerSupport() {
    return new JacksonSwaggerSupport();
  }


}
