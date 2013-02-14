package com.mangofactory.swagger.spring.filters;

import com.mangofactory.swagger.filters.Filter;
import com.mangofactory.swagger.filters.FilterContext;
import com.wordnik.swagger.core.DocumentationParameter;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static com.mangofactory.swagger.spring.Descriptions.*;

public class ParameterFilter implements Filter<DocumentationParameter> {
    @Override
    public void apply(FilterContext<DocumentationParameter> context) {
        DocumentationParameter parameter = context.subject();
        MethodParameter methodParameter = context.get("methodParameter");

        documentParameter(parameter, methodParameter);
    }

    private void documentParameter(DocumentationParameter parameter, MethodParameter methodParameter) {

        String name = selectBestParameterName(methodParameter);
        String description = splitCamelCase(name);
        if (StringUtils.isEmpty(name)) {
            name = methodParameter.getParameterName();
        }
        String paramType = getParameterType(methodParameter);
        String dataType = methodParameter.getParameterType().getSimpleName();
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
        parameter.setAllowableValues(null);
        parameter.setRequired(isRequired);
        parameter.setAllowMultiple(false);
        parameter.setDataType(dataType);

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
