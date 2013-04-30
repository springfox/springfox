package com.mangofactory.swagger.models;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.wordnik.swagger.core.DocumentationSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;

@Slf4j
@Component
public class DocumentationSchemaProvider {

    private final SchemaDescriptor descriptor;
    private TypeResolver typeResolver;

    @Resource(name="customSchemaGenerators")
    private List<CustomSchemaGenerator> customSchemaGenerators;

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
        providers.setCustomVisitors(getCustomSchemaGenerators());
        providers.schema(model.getType());
        return providers.getSchemaMap();
    }

    private List<CustomSchemaGenerator> getCustomSchemaGenerators() {
        if (customSchemaGenerators != null) {
            return customSchemaGenerators;
        }
        return newArrayList();
    }

    @VisibleForTesting
    void setCustomSchemaGenerators(List<CustomSchemaGenerator> customSchemaGenerators) {
        this.customSchemaGenerators = customSchemaGenerators;
    }
}
