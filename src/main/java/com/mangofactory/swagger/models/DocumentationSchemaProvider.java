package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordnik.swagger.core.DocumentationSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DocumentationSchemaProvider {

    private final SchemaDescriptor descriptor;
    private TypeResolver typeResolver;

    @Autowired
    public DocumentationSchemaProvider(TypeResolver typeResolver, SchemaDescriptor descriptor) {
        this.typeResolver = typeResolver;
        this.descriptor = descriptor;
    }

    public DocumentationSchemaProvider(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        this.descriptor = new Jackson2SchemaDescriptor(new ObjectMapper());
    }

    public Map<String, DocumentationSchema> getModelMap(Model model) {
        SchemaProvider providers = new SchemaProvider(descriptor, typeResolver, model.isReturnType());
        providers.schema(model.getType());
        return providers.getSchemaMap();
    }

}
