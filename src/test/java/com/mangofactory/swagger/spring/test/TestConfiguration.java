package com.mangofactory.swagger.spring.test;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.SwaggerConfigurationExtension;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.Jackson2SchemaDescriptor;
import com.mangofactory.swagger.models.SchemaDescriptor;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.google.common.collect.Lists.*;

@Configuration
@EnableWebMvc
@ComponentScan("com.mangofactory.swagger.spring.test")
public class TestConfiguration {

    @Bean
    public DocumentationController documentationController() {
        return new DocumentationController();
    }

    @Bean
    public SwaggerConfiguration swaggerConfiguration() {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
        swaggerConfiguration.setApiVersion("2.0");
        swaggerConfiguration.setBasePath("/some-path");
        swaggerConfiguration.setExcludedResources(newArrayList("/excluded"));
        return swaggerConfiguration;
    }

    @Bean
    public SwaggerConfigurationExtension swaggerConfigurationExtension() {
        return new SwaggerConfigurationExtension();
    }

    @Bean
    @Autowired
    public DocumentationSchemaProvider documentationSchemaProvider(SchemaDescriptor schemaDescriptor) {
        return new DocumentationSchemaProvider(new TypeResolver(), schemaDescriptor);
    }

    @Bean
    public SchemaDescriptor schemaDescriptor() {
        return new Jackson2SchemaDescriptor(new ObjectMapper());
    }

}
