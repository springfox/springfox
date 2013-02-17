package com.mangofactory.swagger.spring.filters;

import com.google.common.base.Function;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.Model;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationAllowableValues;
import com.wordnik.swagger.core.DocumentationParameter;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.spring.Descriptions.*;

public class ParameterFilter implements Filter<DocumentationParameter> {
    @Override
    public void apply(FilterContext<DocumentationParameter> context) {
        DocumentationParameter parameter = context.subject();
        MethodParameter methodParameter = context.get("methodParameter");
        ControllerDocumentation controllerDocumentation = context.get("controllerDocumentation");

        documentParameter(controllerDocumentation, parameter,  methodParameter);
    }

    private void documentParameter(ControllerDocumentation controllerDocumentation, DocumentationParameter parameter,
                                   MethodParameter methodParameter) {

        String name = selectBestParameterName(methodParameter);
        String description = splitCamelCase(name);
        if (StringUtils.isEmpty(name)) {
            name = methodParameter.getParameterName();
        }
        String paramType = getParameterType(methodParameter);
        Class<?> parameterType = methodParameter.getParameterType();
        String dataType = parameterType.getSimpleName();
        parameter.setDataType(dataType);
        maybeAddParameterTypeToModels(controllerDocumentation, parameterType, dataType);
        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        boolean isRequired = false;
        if (requestParam != null) {
            isRequired = requestParam.required();
        }
        parameter.setName(name);
        parameter.setDescription(description);
        parameter.setNotes("");
        parameter.setParamType(paramType);
        parameter.setDefaultValue("");
        parameter.setAllowableValues(maybeGetAllowableValues(parameterType));
        parameter.setRequired(isRequired);
        parameter.setAllowMultiple(false);
        parameter.setDataType(dataType);

    }

    private DocumentationAllowableValues maybeGetAllowableValues(Class<?> parameterType) {
        DocumentationAllowableValues allowableValues = null;
        if (parameterType.isEnum()) {
            allowableValues = new DocumentationAllowableListValues(transform(newArrayList(parameterType
                    .getEnumConstants()), convertToString()));
        }
        return allowableValues;
    }

    private Function<? super Object, String> convertToString() {
        return new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return input.toString();
            }
        };
    }

    private void maybeAddParameterTypeToModels(ControllerDocumentation controllerDocumentation,
                                               Class<?> parameterType, String dataType) {

        if (isKnownType(parameterType)) {
            return;
        }
        if (parameterType.isArray()) {

            String componentType = parameterType.getComponentType().getSimpleName();
            if (isComplexType(parameterType.getComponentType())) {
                controllerDocumentation.putModel(componentType, new Model(String.format("Array[%s]", componentType),
                        parameterType.getComponentType()));
            }

        } else {
            controllerDocumentation.putModel(dataType, new Model(dataType, parameterType));
        }
    }

    private boolean isKnownType(Class<?> parameterType) {
        return parameterType.isAssignableFrom(List.class) ||
                parameterType.isAssignableFrom(Set.class) ||
                parameterType.isPrimitive() ||
                parameterType.isEnum() ||
                parameterType.isAssignableFrom(String.class) ||
                parameterType.isAssignableFrom(Date.class);
    }

    private boolean isComplexType(Class<?> parameterType) {
        return !parameterType.isEnum() &&
                !parameterType.isPrimitive() &&
                !parameterType.isArray() &&
                !parameterType.isAssignableFrom(List.class) &&
                !parameterType.isAssignableFrom(Set.class) &&
                !parameterType.isAssignableFrom(String.class) &&
                !parameterType.isAssignableFrom(Date.class);
    }

    private String getParameterType(MethodParameter methodParameter) {
        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            return "query";
        }
        PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null) {
            return "path";
        }
        RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
        if (requestBody != null) {
            return "body";
        }
        ModelAttribute modelAttribute = methodParameter.getParameterAnnotation(ModelAttribute.class);
        if (modelAttribute != null) {
            return "body";
        }
        return "query";
    }

    private String selectBestParameterName(MethodParameter methodParameter) {
        PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null && !StringUtils.isEmpty(pathVariable.value())) {
            return pathVariable.value();
        }
        ModelAttribute modelAttribute = methodParameter.getParameterAnnotation(ModelAttribute.class);
        if (modelAttribute != null && !StringUtils.isEmpty(modelAttribute.value())) {
            return modelAttribute.value();
        }
        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null && !StringUtils.isEmpty(requestParam.value())) {
            return requestParam.value();
        }
        // Default
        return methodParameter.getParameterName();
    }
}
