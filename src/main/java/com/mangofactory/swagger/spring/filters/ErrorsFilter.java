package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.annotations.ApiError;
import com.mangofactory.swagger.annotations.ApiErrors;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.core.DocumentationError;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

public class ErrorsFilter implements Filter<List<DocumentationError>> {
    @Override
    public void apply(FilterContext<List<DocumentationError>> context) {
        List<DocumentationError> errors = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");

        documentExceptions(errors, handlerMethod);
    }

    private void documentExceptions(List<DocumentationError> errors, HandlerMethod handlerMethod) {
        discoverSpringMvcExceptions(errors, handlerMethod);
        discoverThrowsExceptions(errors, handlerMethod);
    }

    private void discoverThrowsExceptions(List<DocumentationError> error, HandlerMethod handlerMethod) {
        Class<?>[] exceptionTypes = handlerMethod.getMethod().getExceptionTypes();
        for (Class<?> exceptionType : exceptionTypes) {
            appendErrorFromClass(error, (Class<? extends Throwable>) exceptionType);
        }
    }

    private void discoverSpringMvcExceptions(List<DocumentationError> errors, HandlerMethod handlerMethod) {
        ApiErrors apiErrors = handlerMethod.getMethodAnnotation(ApiErrors.class);
        if (apiErrors == null) {
            return;
        }
        for (Class<? extends Throwable> exceptionClass : apiErrors.value()) {
            appendErrorFromClass(errors, exceptionClass);
        }
        for (ApiError apiError : apiErrors.errors()) {
            errors.add(new DocumentationError(apiError.code(), apiError.reason()));
        }
    }

    void appendErrorFromClass(List<DocumentationError> errors, Class<? extends Throwable> exceptionClass) {
        ApiError apiError = exceptionClass.getAnnotation(ApiError.class);
        if (apiError == null) {
            return;
        }
        errors.add(new DocumentationError(apiError.code(), apiError.reason()));
    }
}
