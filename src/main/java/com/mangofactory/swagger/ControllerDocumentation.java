package com.mangofactory.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

@Slf4j
public class ControllerDocumentation extends Documentation {

    private final List<DocumentationEndPoint> endpoints = newArrayList();
    private final Map<String, Model> modelMap = newHashMap();

    public ControllerDocumentation(String apiVersion, String swaggerVersion,
                                   String basePath, String resourceUri) {

        super(apiVersion, swaggerVersion, basePath, resourceUri);
    }


    public void addEndpoint(DocumentationEndPoint endpoint) {
        endpoints.add(endpoint);
    }

    public void putModel(String name, Model model) {
        modelMap.put(name, model);
    }

    public Boolean matchesName(String name) {
        String nameWithForwardSlash = (name.startsWith("/")) ? name : "/" + name;
        String nameWithoutForwardSlash = (name.startsWith("/")) ? name.substring(1) : name;

        return getResourcePath().equals(nameWithoutForwardSlash) ||
                       getResourcePath().equals(nameWithForwardSlash);
    }

    public List<DocumentationOperation> getEndPoint(String requestUri, RequestMethod method) {
        List<DocumentationOperation> operations = newArrayList();
        for (DocumentationEndPoint endPoint : endpoints) {
            if (StringUtils.equals(endPoint.getPath(), requestUri)) {
                for (DocumentationOperation operation : endPoint.getOperations()) {
                    if (operation.getHttpMethod().equals(method.name())) {
                        operations.add(operation);
                    }
                }
            }
        }
        return operations;
    }

    @Override
    public List<DocumentationEndPoint> getApis() {
        return endpoints;
    }

    @Override
    public HashMap<String, DocumentationSchema> getModels() {
        return newHashMap(Maps.transformValues(modelMap, new Function<Model, DocumentationSchema>() {
            @Override
            public DocumentationSchema apply(Model input) {
                ObjectMapper mapper = new ObjectMapper();
                JsonSchema jsonSchema;
                DocumentationSchema schema = new DocumentationSchema();
                schema.setId(input.getName());
                try {
                    jsonSchema = mapper.generateJsonSchema(input.getType());
                    ObjectWriter writer = mapper.writer();
                    Writer stringWriter = new StringWriter();
                    writer.writeValue(stringWriter, jsonSchema);
                    String schemaAsString = stringWriter.toString();
                    schema.setItems(mapper.readValue(schemaAsString, DocumentationSchema.class));
                    return fixup(schema);
                } catch (IOException e) {
                    return schema;
                }

            }
        }));
    }

    private DocumentationSchema fixup(DocumentationSchema schema) {
        DocumentationSchema fixup = new DocumentationSchema();
        fixup.setId(schema.getId());
        fixup.setProperties(schema.getItems().getProperties());
        return fixup;
    }

}
