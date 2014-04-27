package com.mangofactory.swagger.models.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.models.FieldsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = { FieldsProvider.class })
public class SwaggerModelsConfiguration {
    @Bean
    public TypeResolver typeResolver() {
        return new TypeResolver();
    }
}
