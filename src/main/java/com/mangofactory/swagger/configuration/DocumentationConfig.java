package com.mangofactory.swagger.configuration;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.DefaultDocumentationTransformer;
import com.mangofactory.swagger.DocumentationTransformer;
import com.mangofactory.swagger.EndpointComparator;
import com.mangofactory.swagger.OperationComparator;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.SwaggerConfigurationExtension;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.Jackson2SchemaDescriptor;
import com.mangofactory.swagger.models.SchemaDescriptor;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfig {
    @Bean
    public DocumentationController documentationController() {
        return new DocumentationController();
    }

    @Bean
    @Autowired
    public SwaggerConfiguration swaggerConfiguration(DefaultConfigurationModule defaultConfig,
        ExtensibilityModule extensibility, @Value("${documentation.services.basePath}") String basePath,
        @Value("${documentation.services.version}") String apiVersion) {
        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration(apiVersion, basePath);
        return extensibility.apply(defaultConfig.apply(swaggerConfiguration));
    }

    @Bean
    @Autowired
    DefaultConfigurationModule defaultConfigurationModule() {
        return new DefaultConfigurationModule();
    }

    @Bean
    public ExtensibilityModule extensibilityModule() {
        return new ExtensibilityModule();
    }

    @Bean
    public SwaggerConfigurationExtension swaggerConfigurationExtension() {
        return new SwaggerConfigurationExtension();
    }

    @Bean
    public DocumentationTransformer documentationTransformer() {
        return new DefaultDocumentationTransformer(endPointComparator(), operationComparator());
    }

    @Bean
    public OperationComparator operationComparator() {
        return null;
    }

    @Bean
    public EndpointComparator endPointComparator() {
        return null;
    }

    @Bean
    @Autowired
    DocumentationSchemaProvider documentationSchemaProvider(TypeResolver typeResolver,
            SchemaDescriptor schemaDescriptor, SwaggerConfiguration configuration) {
        return new DocumentationSchemaProvider(typeResolver, configuration, schemaDescriptor);
    }

    @Bean
    @Autowired
    public SchemaDescriptor schemaDescriptor(SwaggerConfiguration configuration,
            ObjectMapper documentationObjectMapper) {
        return new Jackson2SchemaDescriptor(configuration, documentationObjectMapper);
    }

    @Bean
    public ObjectMapper documentationObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TypeResolver typeResolver() {
        return new TypeResolver();
    }
}
