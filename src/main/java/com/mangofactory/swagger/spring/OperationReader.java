package com.mangofactory.swagger.spring;

import com.google.common.collect.Lists;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.filters.Filters;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

public class OperationReader {
    private final SwaggerConfiguration configuration;

    public OperationReader(SwaggerConfiguration configuration) {
        this.configuration = configuration;
    }

    DocumentationOperation readOperation(ControllerDocumentation controllerDocumentation, HandlerMethod handlerMethod, RequestMethod requestMethod) {
        DocumentationOperation operation = new DocumentationOperation(requestMethod.name(), "", "");
        FilterContext<DocumentationOperation> operationContext = new FilterContext<DocumentationOperation>(operation);
        operationContext.put("handlerMethod", handlerMethod);
        operationContext.put("controllerDocumentation", controllerDocumentation);
        Filters.Fn.applyFilters(configuration.getOperationFilters(), operationContext);

        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            DocumentationParameter parameter = new DocumentationParameter();
            if (configuration.isParameterTypeIgnoreable(methodParameter.getParameterType())) {
                continue;
            }
            FilterContext<DocumentationParameter> parameterContext = new FilterContext<DocumentationParameter>(parameter);
            parameterContext.put("methodParameter", methodParameter);
            parameterContext.put("controllerDocumentation", controllerDocumentation);
            Filters.Fn.applyFilters(configuration.getParameterFilters(), parameterContext);
            operation.addParameter(parameter);
        }
        List<DocumentationError> errors = Lists.newArrayList();
        FilterContext<List<DocumentationError>> errorContext = new FilterContext<List<DocumentationError>>(errors);
        errorContext.put("handlerMethod", handlerMethod);
        Filters.Fn.applyFilters(configuration.getErrorFilters(), errorContext);
        for (DocumentationError error : errors) {
            operation.addErrorResponse(error);
        }
        return operation;
    }
}