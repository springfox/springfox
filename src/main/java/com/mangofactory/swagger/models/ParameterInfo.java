package com.mangofactory.swagger.models;

import org.springframework.core.MethodParameter;

import com.fasterxml.classmate.ResolvedType;

public class ParameterInfo {
    private MethodParameter methodParameter ;
    private ResolvedType parameterType;
    private String defaultParameterName;
    public MethodParameter getMethodParameter() {
        return methodParameter;
    }
    public void setMethodParameter(MethodParameter methodParameter) {
        this.methodParameter = methodParameter;
    }
    public ResolvedType getParameterType() {
        return parameterType;
    }
    public void setParameterType(ResolvedType parameterType) {
        this.parameterType = parameterType;
    }
    public String getDefaultParameterName() {
        return defaultParameterName;
    }
    public void setDefaultParameterName(String defaultParameterName) {
        this.defaultParameterName = defaultParameterName;
    }

    

}
