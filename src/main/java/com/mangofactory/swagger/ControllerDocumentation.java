package com.mangofactory.swagger;

import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.Model;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationSchema;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Models.Fn.*;

@XmlRootElement
public class ControllerDocumentation extends Documentation {

    private final List<DocumentationEndPoint> endpoints = newArrayList();
    private final Map<String, Model> modelMap = newHashMap();
    private DocumentationSchemaProvider schemaProvider;
    private HashMap<String,DocumentationSchema> models;


    //Used by JAXB
    ControllerDocumentation() {
    }

    public ControllerDocumentation(String apiVersion, String swaggerVersion,
                                   String basePath, String resourceUri, DocumentationSchemaProvider schemaProvider) {

        super(apiVersion, swaggerVersion, basePath, resourceUri);
        this.schemaProvider = schemaProvider;
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
        if (models == null) {
            models = newHashMap();
            for (Model model: modelMap.values()) {
                models.putAll(modelToSchema(schemaProvider).apply(model));
            }
            //Free-up the memory of the types
            modelMap.clear();
        }
        return models;
    }
}
