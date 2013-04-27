package com.mangofactory.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.filters.Filters.Fn.*;

public class SwaggerConfiguration {
    public static final String API_DOCS_PATH = "/api-docs";
    public static final String SWAGGER_VERSION = "1.0";

    @Getter private final String documentationBasePath;
    @Getter private final String swaggerVersion;
    @Getter @Setter private String apiVersion;
    @Getter @Setter private String basePath;

    @Getter private List<String> excludedResources = newArrayList();
    @Getter private final List<Filter<Documentation>> documentationFilters = newArrayList();
    @Getter private final List<Filter<DocumentationEndPoint>> endpointFilters = newArrayList();
    @Getter private final List<Filter<DocumentationOperation>> operationFilters = newArrayList();
    @Getter private final List<Filter<DocumentationParameter>> parameterFilters = newArrayList();
    @Getter private final List<Filter<List<DocumentationError>>> errorFilters = newArrayList();
    @Getter private final List<Class<?>> ignorableParameterTypes = newArrayList();

    @Autowired @Getter private DocumentationTransformer documentationTransformer;
    @Autowired private DocumentationSchemaProvider schemaProvider;
    @Autowired private TypeResolver typeResolver;

    public SwaggerConfiguration(String apiVersion, String basePath) {
        this.swaggerVersion = SWAGGER_VERSION;
        this.documentationBasePath = API_DOCS_PATH;
        this.basePath = basePath;
        this.apiVersion = apiVersion;
    }

    public Documentation newDocumentation(WebApplicationContext webApplicationContext) {
        FilterContext<Documentation> context = new FilterContext<Documentation>(new Documentation(null, swaggerVersion,
                basePath, null));
        context.put("swagger", this);
        context.put("webApplicationContext", webApplicationContext);
        applyFilters(documentationFilters, context);
        return context.subject();
    }

    public boolean isExcluded(String documentationEndpointUri) {
        if(isNullOrEmpty(documentationEndpointUri)) {
            return false;
        }
        String controllerUri = documentationEndpointUri;
        if (documentationEndpointUri.contains(API_DOCS_PATH)) {
           controllerUri = documentationEndpointUri.substring(API_DOCS_PATH.length());
        }
        return excludedResources.contains(controllerUri);
    }

    public boolean isParameterTypeIgnorable(Class<?> parameterType) {
        return ignorableParameterTypes.contains(parameterType);
    }

    public DocumentationSchemaProvider getSchemaProvider() {
        if (schemaProvider == null) {
            schemaProvider = new DocumentationSchemaProvider(getTypeResolver());
        }
        return schemaProvider;
    }

    public TypeResolver getTypeResolver() {
        if (typeResolver == null) {
            typeResolver = new TypeResolver();
        }
        return typeResolver;
    }
}
