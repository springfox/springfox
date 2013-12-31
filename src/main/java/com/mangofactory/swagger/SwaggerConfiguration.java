package com.mangofactory.swagger;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.models.DocumentationSchemaProvider;
import com.mangofactory.swagger.models.TypeProcessingRule;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.filters.Filters.Fn.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.WildcardType.*;

public class SwaggerConfiguration {
    public static final String API_DOCS_PATH = "/api-docs";
    public static final String SWAGGER_VERSION = "1.0";

    private final String documentationBasePath;
    private final String swaggerVersion;

    private final List<Filter<Documentation>> documentationFilters = newArrayList();
    private final List<Filter<DocumentationEndPoint>> endpointFilters = newArrayList();
    private final List<Filter<DocumentationOperation>> operationFilters = newArrayList();
    private final List<Filter<DocumentationParameter>> parameterFilters = newArrayList();
    private final List<Filter<List<DocumentationError>>> errorFilters = newArrayList();
    private final List<TypeProcessingRule> typeProcessingRules = newArrayList();

    @Autowired private DocumentationTransformer documentationTransformer;
    @Autowired private DocumentationSchemaProvider schemaProvider;
    @Autowired private TypeResolver typeResolver;

    private String apiVersion;
    private String basePath;
    private List<String> excludedResources = newArrayList();

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

    public boolean isExcluded(List<String> documentationEndpointUris) {
        if (documentationEndpointUris == null) {
            return false;
        }
        for(String uri: documentationEndpointUris) {
            if(isNullOrEmpty(uri)) {
                return false;
            }
            String controllerUri = uri;
            if (uri.contains(API_DOCS_PATH)) {
               controllerUri = uri.substring(API_DOCS_PATH.length());
            }
            if (excludedResources.contains(controllerUri)) {
                return true;
            }
       }
       return false;
    }

    public boolean isParameterTypeIgnorable(final ResolvedType type) {
        TypeProcessingRule rule = findProcessingRule(type);
        return rule.isIgnorable();
    }

    public ResolvedType maybeGetAlternateType(final ResolvedType parameterType) {
        TypeProcessingRule rule = findProcessingRule(parameterType);
        return rule.alternateType(parameterType);
    }

    private TypeProcessingRule findProcessingRule(final ResolvedType parameterType) {
        return find(typeProcessingRules, new Predicate<TypeProcessingRule>() {
            @Override
            public boolean apply(TypeProcessingRule input) {
                return (hasWildcards(input.originalType()) && wildcardMatch(parameterType, input.originalType()))
                        || exactMatch(input.originalType(), parameterType);
            }
        }, new DefaultProcessingRule(parameterType.getErasedType()));
    }

    public DocumentationSchemaProvider getSchemaProvider() {
        if (schemaProvider == null) {
            schemaProvider = new DocumentationSchemaProvider(getTypeResolver(), this);
        }
        return schemaProvider;
    }

    public TypeResolver getTypeResolver() {
        if (typeResolver == null) {
            typeResolver = new TypeResolver();
        }
        return typeResolver;
    }

    public List<String> getExcludedResources() {
        return excludedResources;
    }

    public String getDocumentationBasePath() {
        return documentationBasePath;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public DocumentationTransformer getDocumentationTransformer() {
        return documentationTransformer;
    }

    public void setDocumentationTransformer(DocumentationTransformer documentationTransformer) {
        this.documentationTransformer = documentationTransformer;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }
    public String getBasePath() {
        return basePath;
    }
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<Filter<Documentation>> getDocumentationFilters() {
        return documentationFilters;
    }

    public List<Filter<DocumentationEndPoint>> getEndpointFilters() {
        return endpointFilters;
    }

    public List<Filter<DocumentationOperation>> getOperationFilters() {
        return operationFilters;
    }

    public List<Filter<DocumentationParameter>> getParameterFilters() {
        return parameterFilters;
    }

    public List<Filter<List<DocumentationError>>> getErrorFilters() {
        return errorFilters;
    }

    public List<TypeProcessingRule> getTypeProcessingRules() {
        return typeProcessingRules;
    }

    static class DefaultProcessingRule implements TypeProcessingRule {
        private Class<?> clazz;

        public DefaultProcessingRule(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean isIgnorable() {
            return false;
        }

        @Override
        public boolean hasAlternateType() {
            return false;
        }

        @Override
        public ResolvedType originalType() {
            return asResolvedType(clazz);
        }

        @Override
        public ResolvedType alternateType(ResolvedType parameterType) {
            return parameterType;
        }
    }
}
