package com.mangofactory.swagger.configuration;

import com.mangofactory.schema.configuration.SwaggerModelsConfiguration;
import com.mangofactory.spring.web.GroupCache;
import com.mangofactory.spring.web.ResourceGroupingStrategy;
import com.mangofactory.spring.web.plugins.ApiListingBuilderPlugin;
import com.mangofactory.spring.web.plugins.Defaults;
import com.mangofactory.spring.web.plugins.DocumentationPlugin;
import com.mangofactory.spring.web.plugins.OperationBuilderPlugin;
import com.mangofactory.spring.web.plugins.ParameterBuilderPlugin;
import com.mangofactory.spring.web.plugins.ParameterExpanderPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@ComponentScan(basePackages = {
        "com.mangofactory.spring.web.scanners",
        "com.mangofactory.spring.web.readers",
        "com.mangofactory.spring.web.plugins",
        "com.mangofactory.swagger.web",
        "com.mangofactory.swagger.plugins"
})
@Import({ SwaggerModelsConfiguration.class, Defaults.class })
@EnablePluginRegistries({DocumentationPlugin.class,
        ApiListingBuilderPlugin.class,
        OperationBuilderPlugin.class,
        ParameterBuilderPlugin.class,
        ParameterExpanderPlugin.class,
        ResourceGroupingStrategy.class})
public class SpringSwaggerConfig {


  @Bean
  public GroupCache resourceGroupCache() {
    return new GroupCache();
  }

  /**
   * Registers some custom serializers needed to transform swagger models to swagger-ui required json format.
   */
  @Bean
  public JacksonSwaggerSupport jacksonSwaggerSupport() {
    return new JacksonSwaggerSupport();
  }

}
