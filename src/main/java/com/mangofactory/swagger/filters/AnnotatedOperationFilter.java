package com.mangofactory.swagger.filters;

import com.google.common.base.Splitter;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.models.Model;
import com.mangofactory.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.core.DocumentationOperation;
import org.springframework.web.method.HandlerMethod;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.annotations.Annotations.*;

public class AnnotatedOperationFilter implements Filter<DocumentationOperation> {
    @Override
    public void apply(FilterContext<DocumentationOperation> context) {
        DocumentationOperation operation = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");
        ControllerDocumentation controllerDocumentation = context.get("controllerDocumentation");

        documentOperation(controllerDocumentation, operation, handlerMethod);
    }

    private void documentOperation(ControllerDocumentation controllerDocumentation, DocumentationOperation operation, HandlerMethod handlerMethod) {
        ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
        if (apiOperation != null) {
            operation.setSummary(apiOperation.value());
            operation.setNotes(apiOperation.notes());
            if (operation.responseClass() == null) {
                if (apiOperation.multiValueResponse()) {
                    operation.setResponseClass(String.format("Array[%s]", apiOperation.responseClass()));
                } else {
                    operation.setResponseClass(apiOperation.responseClass());
                }
            }
            operation.setTags(newArrayList(Splitter.on(",").omitEmptyStrings().split(apiOperation.tags())));
        }
        ApiModel apiModel = handlerMethod.getMethodAnnotation(ApiModel.class);
        if (apiModel != null) {
            operation.setResponseClass(getAnnotatedType(apiModel));
            String simpleName = apiModel.type().getSimpleName();
            controllerDocumentation.putModel(simpleName, new Model(simpleName,  apiModel.type(), true));
        }
    }

}
