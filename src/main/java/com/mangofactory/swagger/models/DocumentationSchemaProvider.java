package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.wordnik.swagger.core.DocumentationSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DocumentationSchemaProvider {

    private final SwaggerConfiguration configuration;
    private final SchemaDescriptor descriptor;
    private final TypeResolver typeResolver;

    @Autowired
    public DocumentationSchemaProvider(TypeResolver typeResolver, SwaggerConfiguration configuration, SchemaDescriptor
                                       descriptor) {
        this.typeResolver = typeResolver;
        this.configuration = configuration;
        this.descriptor = descriptor;
    }

    public DocumentationSchemaProvider(TypeResolver typeResolver, SwaggerConfiguration configuration) {
        this.typeResolver = typeResolver;
        this.configuration = configuration;
        this.descriptor = new Jackson2SchemaDescriptor(configuration, new ObjectMapper());
    }

    public Map<String, DocumentationSchema> getModelMap(Model model) {
        SchemaProvider providers = new SchemaProvider(configuration, descriptor, typeResolver, model.isReturnType());
        providers.schema(model.getType());
        return providers.getSchemaMap();
    }

}
