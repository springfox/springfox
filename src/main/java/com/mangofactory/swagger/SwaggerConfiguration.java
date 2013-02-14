package com.mangofactory.swagger;

import com.mangofactory.swagger.filters.AnnotatedEndpointFilter;
import com.mangofactory.swagger.filters.AnnotatedErrorsFilter;
import com.mangofactory.swagger.filters.AnnotatedOperationFilter;
import com.mangofactory.swagger.filters.AnnotatedParameterFilter;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.spring.filters.ApplicationFilter;
import com.mangofactory.swagger.spring.filters.EndPointFilter;
import com.mangofactory.swagger.spring.filters.ErrorsFilter;
import com.mangofactory.swagger.spring.filters.OperationFilter;
import com.mangofactory.swagger.spring.filters.ParameterFilter;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.filters.Filters.Fn.*;

public class SwaggerConfiguration  implements InitializingBean {
    public static final String API_DOCS_PATH = "/api-docs";
    public static final String SWAGGER_VERSION = "1.0";
    @Getter private final String documentationBasePath;
    @Getter private final String swaggerVersion;
    @Getter @Setter private SwaggerConfigurationExtension extensions;
    @Getter @Setter private String apiVersion;
    @Getter @Setter private String basePath;
    @Setter private List<String> excludedResources;
    @Getter private final List<Filter<Documentation>> documentationFilters = newArrayList();
    @Getter private final List<Filter<DocumentationEndPoint>> endpointFilters = newArrayList();
    @Getter private final List<Filter<DocumentationOperation>> operationFilters = newArrayList();
    @Getter private final List<Filter<DocumentationParameter>> parameterFilters = newArrayList();
    @Getter private final List<Filter<List<DocumentationError>>> errorFilters = newArrayList();

    public SwaggerConfiguration(boolean applyDefaults) {
        this.swaggerVersion = SWAGGER_VERSION;
        this.documentationBasePath = API_DOCS_PATH;
        this.excludedResources = newArrayList();
        if(applyDefaults) {
            applyDefaults();
        }
    }

    public SwaggerConfiguration() {
       this(true);
    }

    private void applyDefaults() {
        Filter<Documentation> applicationDocumentationFilter = new ApplicationFilter();
        documentationFilters.add(applicationDocumentationFilter);

        Filter<DocumentationEndPoint> endPointFilter = new EndPointFilter();
        Filter<DocumentationEndPoint> annotationEndpointFilter = new AnnotatedEndpointFilter();
        endpointFilters.addAll(newArrayList(endPointFilter, annotationEndpointFilter));

        Filter<DocumentationOperation> operationFilter = new OperationFilter();
        Filter<DocumentationOperation> annotatedOperationFilter = new AnnotatedOperationFilter();
        operationFilters.addAll(newArrayList(operationFilter, annotatedOperationFilter));

        Filter<DocumentationParameter> parameterFilter = new ParameterFilter();
        Filter<DocumentationParameter> annotatedParameterFilter = new AnnotatedParameterFilter();
        parameterFilters.addAll(newArrayList(parameterFilter, annotatedParameterFilter));

        Filter<List<DocumentationError>> errorFilter = new ErrorsFilter();
        Filter<List<DocumentationError>> annotatedErrorFilter = new AnnotatedErrorsFilter();
        errorFilters.addAll(newArrayList(errorFilter, annotatedErrorFilter));
    }

    public Documentation newDocumentation() {
        FilterContext<Documentation> context = new FilterContext<Documentation>(new Documentation(null, swaggerVersion,
                basePath, null));
        context.put("swagger", this);
        applyFilters(documentationFilters, context);
        return context.subject();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (extensions != null) {
            documentationFilters.addAll(extensions.getDocumentationFilters());
            endpointFilters.addAll(extensions.getEndpointFilters());
            operationFilters.addAll(extensions.getOperationFilters());
            parameterFilters.addAll(extensions.getParameterFilters());
            errorFilters.addAll(extensions.getErrorFilters());
        }
    }

    public boolean isExcluded(String controllerUri) {
        return excludedResources.contains(controllerUri);
    }
}
