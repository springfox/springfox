package springdox.documentation.spring.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import springdox.documentation.schema.configuration.ModelsConfiguration;
import springdox.documentation.spi.service.ApiListingBuilderPlugin;
import springdox.documentation.spi.service.DefaultsProviderPlugin;
import springdox.documentation.spi.service.DocumentationPlugin;
import springdox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.OperationModelsProviderPlugin;
import springdox.documentation.spi.service.ParameterBuilderPlugin;
import springdox.documentation.spi.service.ResourceGroupingStrategy;
import springdox.documentation.spi.service.contexts.Defaults;

@Configuration
@Import({ ModelsConfiguration.class })
@ComponentScan(basePackages = {
        "springdox.documentation.spring.web.scanners",
        "springdox.documentation.spring.web.readers.operation",
        "springdox.documentation.spring.web.readers.parameter",
        "springdox.documentation.spring.web.plugins"
})
@EnablePluginRegistries({DocumentationPlugin.class,
        ApiListingBuilderPlugin.class,
        OperationBuilderPlugin.class,
        ParameterBuilderPlugin.class,
        ExpandedParameterBuilderPlugin.class,
        ResourceGroupingStrategy.class,
        OperationModelsProviderPlugin.class,
        DefaultsProviderPlugin.class})
public class SpringMvcDocumentationConfiguration {

  @Bean
  public Defaults defaults() {
    return new Defaults();
  }

  @Bean
  public DocumentationCache resourceGroupCache() {
    return new DocumentationCache();
  }

}