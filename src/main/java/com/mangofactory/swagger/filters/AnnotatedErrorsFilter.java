package com.mangofactory.swagger.filters;

import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.core.DocumentationError;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

public class AnnotatedErrorsFilter implements Filter<List<DocumentationError>> {
    @Override
    public void apply(FilterContext<List<DocumentationError>> context) {
        List<DocumentationError> errors = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");

        discoverSwaggerAnnotatedExceptions(errors, handlerMethod);
    }

    private void discoverSwaggerAnnotatedExceptions(List<DocumentationError> errors, HandlerMethod handlerMethod) {
        ApiErrors apiErrors = handlerMethod.getMethodAnnotation(ApiErrors.class);
        if (apiErrors == null) {
            return;
        }
        for (ApiError apiError : apiErrors.value()) {
            errors.add(new DocumentationError(apiError.code(), apiError.reason()));
        }
    }
}
