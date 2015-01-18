package com.mangofactory.documentation.schema.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.spi.schema.ModelBuilderPlugin;
import com.mangofactory.documentation.spi.schema.ModelPropertyBuilderPlugin;
import com.mangofactory.documentation.spi.schema.TypeNameProviderPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@ComponentScan(basePackages = {
        "com.mangofactory.documentation.schema.plugins",
        "com.mangofactory.documentation.schema",
        "com.mangofactory.swagger.plugins",
        "com.mangofactory.documentation.swagger.dto.mappers"
})
@EnablePluginRegistries({
        ModelBuilderPlugin.class,
        ModelPropertyBuilderPlugin.class,
        TypeNameProviderPlugin.class
})
public class SwaggerModelsConfiguration {
  @Bean
  public TypeResolver typeResolver() {
    return new TypeResolver();
  }

}
