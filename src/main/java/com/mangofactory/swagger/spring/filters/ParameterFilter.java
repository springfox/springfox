package com.mangofactory.swagger.spring.filters;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.core.DocumentationAllowableListValues;
import com.wordnik.swagger.core.DocumentationAllowableValues;
import com.wordnik.swagger.core.DocumentationParameter;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.mangofactory.swagger.models.Models.isPrimitive;
import static com.mangofactory.swagger.models.Models.maybeAddParameterTypeToModels;
import static com.mangofactory.swagger.models.ResolvedTypes.modelName;
import static com.mangofactory.swagger.spring.Descriptions.splitCamelCase;

public class ParameterFilter implements Filter<DocumentationParameter> {
    @Override
    public void apply(FilterContext<DocumentationParameter> context) {
        DocumentationParameter parameter = context.subject();
        MethodParameter methodParameter = context.get("methodParameter");
        ResolvedType parameterType = context.get("parameterType");
        String defaultParameterName = context.get("defaultParameterName");
        ControllerDocumentation controllerDocumentation = context.get("controllerDocumentation");

        documentParameter(controllerDocumentation, parameter, methodParameter, parameterType, defaultParameterName);
    }

    private void documentParameter(ControllerDocumentation controllerDocumentation, DocumentationParameter parameter,
        MethodParameter methodParameter, ResolvedType parameterType, String defaultParameterName) {

        String name = selectBestParameterName(methodParameter, defaultParameterName);
        String description = selectBestParameterDescription(methodParameter, defaultParameterName);
        if (StringUtils.isEmpty(name)) {
            name = methodParameter.getParameterName();
        }
        //Multi-part file trumps any other annotations
        if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
            parameter.setParamType("body");
            parameter.setDataType("file");
            return;
        }
        String paramType = getParameterType(methodParameter, parameterType);
        String dataType = modelName(parameterType);
        parameter.setDataType(dataType);
        maybeAddParameterTypeToModels(controllerDocumentation, parameterType, dataType, false);
        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);

        boolean isRequired = paramType.equals( "path" );
        if ( !isRequired && requestParam != null ) {
            isRequired = requestParam.required() || !requestParam.defaultValue().equals( ValueConstants.DEFAULT_NONE );
        }

        parameter.setName(name);
        parameter.setDescription(description);
        parameter.setNotes("");
        parameter.setParamType(paramType);
        parameter.setDefaultValue(getDefaultValue(requestParam));
        parameter.setAllowableValues(maybeGetAllowableValues(parameterType.getErasedType()));
        parameter.setRequired(isRequired);
        parameter.setAllowMultiple(false);
        parameter.setDataType(dataType);

    }

    private String getDefaultValue( RequestParam requestParam ) {
        if ( requestParam != null && !requestParam.defaultValue().equals( ValueConstants.DEFAULT_NONE ) ) {
            return requestParam.defaultValue();
        } else {
            return "";
        }
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



    private String getParameterType(MethodParameter methodParameter, ResolvedType parameterType) {
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
        RequestHeader requestHeader = methodParameter.getParameterAnnotation(RequestHeader.class);
        if (requestHeader != null) {
            return "header";
        }
        if (isPrimitive(parameterType.getErasedType())) {
            return "query";
        }
        return "body";
    }

    private String selectBestParameterDescription( MethodParameter methodParameter, String defaultParameterName ) {
        String description = splitCamelCase( selectBestParameterName( methodParameter, defaultParameterName ) );

        ApiParam apiParam = methodParameter.getParameterAnnotation( ApiParam.class );
        if ( apiParam != null && !StringUtils.isEmpty( apiParam.value() ) ) {
            description = apiParam.value();
        }

        return description;
    }

    private String selectBestParameterName(MethodParameter methodParameter, String defaultParameterName) {
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
        RequestHeader requestHeader = methodParameter.getParameterAnnotation(RequestHeader.class);
        if (requestHeader != null && !StringUtils.isEmpty(requestHeader.value())) {
            return requestHeader.value();
        }
        if (!isNullOrEmpty(defaultParameterName)) {
            return defaultParameterName;
        }
        if (isNullOrEmpty(methodParameter.getParameterName())) {
            return String.format("param%s", methodParameter.getParameterIndex());
        }
        return methodParameter.getParameterName();
    }
}
