package com.mangofactory.swagger.spring.filters;

import com.fasterxml.classmate.ResolvedType;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.core.DocumentationOperation;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.models.Models.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.spring.Descriptions.*;

public class OperationFilter implements Filter<DocumentationOperation> {
    @Override
    public void apply(FilterContext<DocumentationOperation> context) {
        DocumentationOperation operation = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");
        ControllerDocumentation controllerDocumentation = context.get("controllerDocumentation");
        SwaggerConfiguration configuration = context.get("swaggerConfiguration");
        documentOperation(configuration, controllerDocumentation, operation, handlerMethod);

    }

    private void documentOperation(SwaggerConfiguration configuration, ControllerDocumentation controllerDocumentation,
                                   DocumentationOperation operation, HandlerMethod handlerMethod) {
        operation.setSummary(splitCamelCase(handlerMethod.getMethod().getName()));
        operation.setNotes("");

        operation.setNickname(handlerMethod.getMethod().getName());
        operation.setDeprecated(handlerMethod.getMethodAnnotation(Deprecated.class) != null);
        ResolvedType parameterType = methodReturnType(configuration.getTypeResolver(), handlerMethod.getMethod());
        if (parameterType != null) {
            ResolvedType alternateType = configuration.maybeGetAlternateType(parameterType);
            if (configuration.isParameterTypeIgnorable(alternateType)) {
                return;
            }
            operation.setResponseClass(modelName(alternateType));
            maybeAddParameterTypeToModels(controllerDocumentation, alternateType, modelName(alternateType), true);
        }

    }
}
