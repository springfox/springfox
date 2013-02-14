package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.core.DocumentationOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

import static com.mangofactory.swagger.spring.Descriptions.*;

@Slf4j
public class OperationFilter implements Filter<DocumentationOperation> {
    @Override
    public void apply(FilterContext<DocumentationOperation> context) {
        DocumentationOperation operation = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");

        documentOperation(operation, handlerMethod);

    }

    private void documentOperation(DocumentationOperation operation,
                                   HandlerMethod handlerMethod) {
        operation.setSummary(splitCamelCase(handlerMethod.getMethod().getName()));
        operation.setNotes("");

        operation.setNickname(handlerMethod.getMethod().getName());
        operation.setDeprecated(handlerMethod.getMethodAnnotation(Deprecated.class) != null);
        operation.setResponseClass(handlerMethod.getReturnType().getParameterType().getSimpleName());
    }
}
