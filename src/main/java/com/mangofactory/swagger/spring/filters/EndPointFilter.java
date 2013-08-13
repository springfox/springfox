package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.core.DocumentationEndPoint;
import lombok.extern.slf4j.Slf4j;

import static com.mangofactory.swagger.spring.Descriptions.*;

@Slf4j
public class EndPointFilter implements Filter<DocumentationEndPoint> {
    @Override
    public void apply(FilterContext<DocumentationEndPoint> context) {
        DocumentationEndPoint doc = context.subject();
        Class<?> controllerClass = context.get("controllerClass");
        doc.setDescription(getDescription(controllerClass));
    }

    private String getDescription(Class<?> controllerClass) {
        return splitCamelCase(controllerClass.getSimpleName());
    }
}
