package com.mangofactory.swagger.filters;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.SwaggerConfiguration;
import com.mangofactory.swagger.annotations.ApiModel;
import com.mangofactory.swagger.models.Model;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.core.DocumentationOperation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.annotations.Annotations.*;
import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class AnnotatedOperationFilter implements Filter<DocumentationOperation> {
    private static final Logger LOG = LogManager.getLogger(AnnotatedOperationFilter.class);

    @Override
    public void apply(FilterContext<DocumentationOperation> context) {
        DocumentationOperation operation = context.subject();
        HandlerMethod handlerMethod = context.get("handlerMethod");
        ControllerDocumentation controllerDocumentation = context.get("controllerDocumentation");
        SwaggerConfiguration swaggerConfiguration  = context.get("swaggerConfiguration");
        documentOperation(controllerDocumentation, operation, handlerMethod, swaggerConfiguration);
    }

    private void documentOperation(ControllerDocumentation controllerDocumentation, DocumentationOperation operation,
                                   HandlerMethod handlerMethod, SwaggerConfiguration configuration) {

        ApiOperation apiOperation = handlerMethod.getMethodAnnotation(ApiOperation.class);
        ResolvedType resolvedType = null;
        ResolvedType parameterType = methodReturnType(configuration.getTypeResolver(), handlerMethod.getMethod());
        if (parameterType != null) {
            resolvedType = configuration.maybeGetAlternateType(parameterType);
        }
        if (apiOperation != null) {
            operation.setSummary(apiOperation.value());
            operation.setNotes(apiOperation.notes());
            if (resolvedType != null && resolvedType.getErasedType() == null) {
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
            if (resolvedType == null || Objects.equal(resolvedType.getErasedType(), getAnnotatedType(apiModel))) {
                operation.setResponseClass(getAnnotatedType(apiModel));
                ResolvedType apiModelAsResolvedType = asResolvedType(apiModel.type());
                String simpleName = modelName(apiModelAsResolvedType);
                controllerDocumentation.putModel(simpleName, new Model(simpleName, apiModelAsResolvedType, true));
            } else {
                LOG.warn("Api Model override does not match the resolved type");
            }

        }
    }

}
