package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.filters.Filters;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class OperationReader {
    private final SwaggerConfiguration configuration;

    public OperationReader(SwaggerConfiguration configuration) {
        this.configuration = configuration;
    }

    DocumentationOperation readOperation(ControllerDocumentation controllerDocumentation, HandlerMethod handlerMethod,
                                         ParamsRequestCondition paramsCondition, RequestMethod requestMethod) {
        DocumentationOperation operation = new DocumentationOperation(requestMethod.name(), "", "");
        FilterContext<DocumentationOperation> operationContext = new FilterContext<DocumentationOperation>(operation);
        operationContext.put("handlerMethod", handlerMethod);
        operationContext.put("controllerDocumentation", controllerDocumentation);
        Filters.Fn.applyFilters(configuration.getOperationFilters(), operationContext);
        int parameterIndex = 0;
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            DocumentationParameter parameter = new DocumentationParameter();
            if (configuration.isParameterTypeIgnoreable(methodParameter.getParameterType())) {
                continue;
            }
            FilterContext<DocumentationParameter> parameterContext = new FilterContext<DocumentationParameter>(parameter);
            parameterContext.put("methodParameter", methodParameter);
            parameterContext.put("parameterIndex", parameterIndex++);
            parameterContext.put("controllerDocumentation", controllerDocumentation);
            Filters.Fn.applyFilters(configuration.getParameterFilters(), parameterContext);
            operation.addParameter(parameter);
        }
        for (NameValueExpression<String> expression : paramsCondition.getExpressions()) {
            if (expression.isNegated()) {
                continue;
            }
            DocumentationParameter parameter = new DocumentationParameter();
            parameter.setDataType("String");
            parameter.setName(expression.getName());
            parameter.setDefaultValue(expression.getValue());
            parameter.setRequired(true);
            parameter.setParamType("query");
            parameter.setAllowableValues(new DocumentationAllowableListValues(newArrayList(expression.getValue())));
            operation.addParameter(parameter);
        }

        List<DocumentationError> errors = newArrayList();
        FilterContext<List<DocumentationError>> errorContext = new FilterContext<List<DocumentationError>>(errors);
        errorContext.put("handlerMethod", handlerMethod);
        Filters.Fn.applyFilters(configuration.getErrorFilters(), errorContext);
        for (DocumentationError error : errors) {
            operation.addErrorResponse(error);
        }
        return operation;
    }
}