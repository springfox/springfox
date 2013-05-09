package com.mangofactory.swagger.configuration;

import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.models.TypeProcessingRule;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

import java.util.List;

public class ExtensibilityModule {
    public SwaggerConfiguration apply(SwaggerConfiguration configuration) {
        customizeDocumentationFilters(configuration.getDocumentationFilters());
        customizeEndpointFilters(configuration.getEndpointFilters());
        customizeOperationFilters(configuration.getOperationFilters());
        customizeParameterFilters(configuration.getParameterFilters());
        customizeErrorFilters(configuration.getErrorFilters());
        customizeTypeProcessingRules(configuration.getTypeProcessingRules());
        customizeExcludedResources(configuration.getExcludedResources());
        return configuration;
    }

    protected void customizeExcludedResources(List<String> excludedResources) {
    }

    protected void customizeTypeProcessingRules(List<TypeProcessingRule> typeProcessingRules) {
    }

    protected void customizeErrorFilters(List<Filter<List<DocumentationError>>> errorFilters) {
    }

    protected void customizeParameterFilters(List<Filter<DocumentationParameter>> parameterFilters) {
    }

    protected void customizeOperationFilters(List<Filter<DocumentationOperation>> operationFilters) {
    }

    protected void customizeEndpointFilters(List<Filter<DocumentationEndPoint>> endpointFilters) {
    }

    protected void customizeDocumentationFilters(List<Filter<Documentation>> documentationFilters) {
    }
}
