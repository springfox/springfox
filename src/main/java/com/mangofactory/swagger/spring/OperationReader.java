package com.mangofactory.swagger.spring;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.models.ResolvedTypes.methodParameters;

import java.util.List;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.FilterContext;
import com.mangofactory.swagger.filters.Filters;
import com.mangofactory.swagger.models.ParameterInfo;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationError;
import com.wordnik.swagger.core.DocumentationOperation;
import com.wordnik.swagger.core.DocumentationParameter;

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
        operationContext.put("swaggerConfiguration", configuration);
        Filters.Fn.applyFilters(configuration.getOperationFilters(), operationContext);
        
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        String [] parameterNames = getParameterNames(handlerMethod, methodParameters.length);
        
        List<ParameterInfo> resolvedParameters = methodParameters(configuration.getTypeResolver(),
                handlerMethod.getMethod(), methodParameters, parameterNames);

        for (int index = 0; index < resolvedParameters.size(); index++) {
            DocumentationParameter parameter = new DocumentationParameter();
            ParameterInfo info = resolvedParameters.get(index);
            ResolvedType resolvedType = configuration.maybeGetAlternateType(info.getParameterType());
            if (resolvedParameters.size() == 0
                    || configuration.isParameterTypeIgnorable(resolvedType)) {
                continue;
            }
            FilterContext<DocumentationParameter> parameterContext
                    = new FilterContext<DocumentationParameter>(parameter);
            parameterContext.put("methodParameter", info.getMethodParameter());
            parameterContext.put("parameterType", resolvedType);
            parameterContext.put("defaultParameterName", info.getDefaultParameterName());
            parameterContext.put("controllerDocumentation", controllerDocumentation);
            Filters.Fn.applyFilters(configuration.getParameterFilters(), parameterContext);
            operation.addParameter(parameter);
        }
        for (NameValueExpression<String> expression : paramsCondition.getExpressions()) {
            if (expression.isNegated() || any(nullToEmptyList(operation.getParameters()),
                    withName(expression.getName()))) {
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

    private Iterable<DocumentationParameter> nullToEmptyList(List<DocumentationParameter> parameters) {
        if (parameters == null) {
            return newArrayList();
        }
        return parameters;
    }

    private String[] getParameterNames(HandlerMethod handlerMethod, int length) {
        String[] parameterNames
                = new LocalVariableTableParameterNameDiscoverer().getParameterNames(handlerMethod.getMethod());
        //Makeup names if it is null
        if (parameterNames == null) {
            parameterNames = new String[length];
            for (int index = 0; index < length; index++) {
                parameterNames[index] = String.format("p%s", index);
            }
        }
        return parameterNames;
    }

    private Predicate<? super DocumentationParameter> withName(final String name) {
        return new Predicate<DocumentationParameter>() {
            @Override
            public boolean apply(DocumentationParameter input) {
                return Objects.equal(input.getName(), name);
            }
        };
    }
}
