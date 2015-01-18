package com.mangofactory.documentation.swagger.configuration;

import com.mangofactory.documentation.schema.configuration.SwaggerModelsConfiguration;
import com.mangofactory.documentation.spring.web.GroupCache;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import com.mangofactory.documentation.spi.service.ApiListingBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.Defaults;
import com.mangofactory.documentation.spi.service.DocumentationPlugin;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.OperationModelsProviderPlugin;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.ParameterExpanderPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.spring.web.scanners",
        "com.mangofactory.documentation.spring.web.readers.operation",
        "com.mangofactory.documentation.spring.web.readers.parameter",
        "com.mangofactory.documentation.spring.web.plugins",
        "com.mangofactory.documentation.swagger.web",
        "com.mangofactory.documentation.swagger.readers.operation",
        "com.mangofactory.documentation.swagger.readers.parameter",
        "com.mangofactory.documentation.swagger.schema"
})
@Import({ SwaggerModelsConfiguration.class, Defaults.class })
@EnablePluginRegistries({DocumentationPlugin.class,
        ApiListingBuilderPlugin.class,
        OperationBuilderPlugin.class,
        ParameterBuilderPlugin.class,
        ParameterExpanderPlugin.class,
        ResourceGroupingStrategy.class,
        OperationModelsProviderPlugin.class})
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
