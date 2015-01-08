package com.mangofactory.schema.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.documentation.plugins.ModelBuilderPlugin;
import com.mangofactory.documentation.plugins.ModelPropertyBuilderPlugin;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.alternates.WildcardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.config.EnablePluginRegistries;

import java.util.Map;

import static com.mangofactory.schema.alternates.Alternates.*;

@Configuration
@ComponentScan(basePackages = {
        "com.mangofactory.schema",
        "com.mangofactory.swagger.dto.mappers",
        "com.mangofactory.documentation.plugins",
        "com.mangofactory.swagger.plugins"
})
@EnablePluginRegistries({
        ModelBuilderPlugin.class,
        ModelPropertyBuilderPlugin.class
})
public class SwaggerModelsConfiguration {
  @Bean
  public TypeResolver typeResolver() {
    return new TypeResolver();
  }

  @Bean(name = "defaultAlternateTypeProvider")
  @Autowired
  public AlternateTypeProvider alternateTypeProvider(TypeResolver typeResolver) {
    AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider();
    alternateTypeProvider.addRule(newRule(typeResolver.resolve(Map.class), typeResolver.resolve(Object.class)));
    alternateTypeProvider.addRule(newRule(typeResolver.resolve(Map.class, String.class, Object.class),
            typeResolver.resolve(Object.class)));
    alternateTypeProvider.addRule(newRule(typeResolver.resolve(Map.class, Object.class, Object.class),
            typeResolver.resolve(Object.class)));
    alternateTypeProvider.addRule(newRule(typeResolver.resolve(Map.class, String.class, String.class),
            typeResolver.resolve(Object.class)));
    alternateTypeProvider.addRule(newMapRule(WildcardType.class, WildcardType.class));

    return alternateTypeProvider;
  }
}
