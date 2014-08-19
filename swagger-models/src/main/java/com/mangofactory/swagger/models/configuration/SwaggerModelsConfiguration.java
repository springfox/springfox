package com.mangofactory.swagger.models.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.alternates.WildcardType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.mangofactory.swagger.models.alternates.Alternates.*;

@Configuration
@ComponentScan(basePackages = {"com.mangofactory.swagger.models"})
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
