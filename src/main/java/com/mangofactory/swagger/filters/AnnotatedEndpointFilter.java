package com.mangofactory.swagger.filters;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.core.DocumentationEndPoint;

public class AnnotatedEndpointFilter implements Filter<DocumentationEndPoint> {
    @Override
    public void apply(FilterContext<DocumentationEndPoint> context) {
        DocumentationEndPoint doc = context.subject();
        Class<?> controllerClass = context.get("controllerClass");
        doc.setDescription(getDescription(controllerClass));
    }


    private String getDescription(Class<?> controllerClass) {
        Api apiAnnotation = controllerClass.getAnnotation(Api.class);
        if (apiAnnotation == null) {
            return "";
        }
        return apiAnnotation.description();

    }

}
