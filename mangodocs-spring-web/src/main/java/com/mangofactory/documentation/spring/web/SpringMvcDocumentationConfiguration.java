package com.mangofactory.documentation.spring.web;

import com.mangofactory.documentation.schema.configuration.ModelsConfiguration;
import com.mangofactory.documentation.spi.service.ApiListingBuilderPlugin;
import com.mangofactory.documentation.spi.service.DocumentationPlugin;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.OperationModelsProviderPlugin;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.ExpandedParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import com.mangofactory.documentation.spi.service.contexts.Defaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@Import({ ModelsConfiguration.class })
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.spring.web.scanners",
        "com.mangofactory.documentation.spring.web.readers.operation",
        "com.mangofactory.documentation.spring.web.readers.parameter",
        "com.mangofactory.documentation.spring.web.plugins"
})
@EnablePluginRegistries({DocumentationPlugin.class,
        ApiListingBuilderPlugin.class,
        OperationBuilderPlugin.class,
        ParameterBuilderPlugin.class,
        ExpandedParameterBuilderPlugin.class,
        ResourceGroupingStrategy.class,
        OperationModelsProviderPlugin.class})
public class SpringMvcDocumentationConfiguration {

  @Bean
  public Defaults defaults() {
    return new Defaults();
  }

  @Bean
  public GroupCache resourceGroupCache() {
    return new GroupCache();
  }

}