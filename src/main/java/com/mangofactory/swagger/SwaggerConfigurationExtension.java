package com.mangofactory.swagger;

import com.mangofactory.swagger.filters.Filter;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.*;

public class SwaggerConfigurationExtension {
    private List<Filter<Documentation>> documentationFilters = newArrayList();
    private List<Filter<DocumentationEndPoint>> endpointFilters = newArrayList();
    private List<Filter<DocumentationOperation>> operationFilters = newArrayList();
    private List<Filter<DocumentationParameter>> parameterFilters = newArrayList();
    private List<Filter<List<DocumentationError>>> errorFilters = newArrayList();
    private List<Class<?>> ignorableParameterTypes = newArrayList();
    private DocumentationTransformer documentationTransformer;
    private Comparator<DocumentationEndPoint> endPointComparator;
    private Comparator<DocumentationOperation> operationComparator;

    public List<Filter<Documentation>> getDocumentationFilters() {
        return documentationFilters;
    }

    public void setDocumentationFilters(List<Filter<Documentation>> documentationFilters) {
        this.documentationFilters = documentationFilters;
    }

    public List<Filter<DocumentationEndPoint>> getEndpointFilters() {
        return endpointFilters;
    }

    public void setEndpointFilters(List<Filter<DocumentationEndPoint>> endpointFilters) {
        this.endpointFilters = endpointFilters;
    }

    public List<Filter<DocumentationOperation>> getOperationFilters() {
        return operationFilters;
    }

    public void setOperationFilters(List<Filter<DocumentationOperation>> operationFilters) {
        this.operationFilters = operationFilters;
    }

    public List<Filter<DocumentationParameter>> getParameterFilters() {
        return parameterFilters;
    }

    public void setParameterFilters(List<Filter<DocumentationParameter>> parameterFilters) {
        this.parameterFilters = parameterFilters;
    }

    public List<Filter<List<DocumentationError>>> getErrorFilters() {
        return errorFilters;
    }

    public void setErrorFilters(List<Filter<List<DocumentationError>>> errorFilters) {
        this.errorFilters = errorFilters;
    }

    public List<Class<?>> getIgnorableParameterTypes() {
        return ignorableParameterTypes;
    }

    public void setIgnorableParameterTypes(List<Class<?>> ignorableParameterTypes) {
        this.ignorableParameterTypes = ignorableParameterTypes;
    }

    public DocumentationTransformer getDocumentationTransformer() {
        return documentationTransformer;
    }

    public void setDocumentationTransformer(DocumentationTransformer documentationTransformer) {
        this.documentationTransformer = documentationTransformer;
    }

    public Comparator<DocumentationEndPoint> getEndPointComparator() {
        return endPointComparator;
    }

    public void setEndPointComparator(Comparator<DocumentationEndPoint> endPointComparator) {
        this.endPointComparator = endPointComparator;
    }

    public Comparator<DocumentationOperation> getOperationComparator() {
        return operationComparator;
    }

    public void setOperationComparator(Comparator<DocumentationOperation> operationComparator) {
        this.operationComparator = operationComparator;
    }
}
