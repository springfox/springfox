package com.mangofactory.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Parameter {
  @JsonProperty
  @JsonUnwrapped
  private SwaggerDataType parameterType;
  private String name;
  private String description;
  private String defaultValue;
  private Boolean required;
  private Boolean allowMultiple;
  @JsonProperty
  @JsonUnwrapped
  private AllowableValues allowableValues;
  private String paramType;
  private String paramAccess;

  public Parameter() {
  }

  public Parameter(String name, String description, String defaultValue, Boolean required, Boolean allowMultiple,
                   String dataType, AllowableValues allowableValues, String paramType, String paramAccess) {
    this.description = description;
    this.defaultValue = defaultValue;
    this.required = required;
    this.allowMultiple = allowMultiple;
    this.allowableValues = allowableValues;
    this.paramType = paramType;
    this.paramAccess = paramAccess;
    this.name = maybeOverrideName(name);
    this.parameterType = new DataType(dataType);
  }

  private String maybeOverrideName(String aName) {
    if ("body".equals(paramType)) {
      return paramType;
    }
    return aName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Boolean isRequired() {
    return required;
  }

  public Boolean isAllowMultiple() {
    return allowMultiple;
  }

  public AllowableValues getAllowableValues() {
    return allowableValues;
  }

  public void setAllowableValues(AllowableValues allowableValues) {
    this.allowableValues = allowableValues;
  }

  public String getParamType() {
    return paramType;
  }

  public void setParamType(String paramType) {
    this.paramType = paramType;
  }

  public String getParamAccess() {
    return paramAccess;
  }

  public void setParamAccess(String paramAccess) {
    this.paramAccess = paramAccess;
  }

  public SwaggerDataType getParameterType() {
    return parameterType;
  }

  public void setParameterType(SwaggerDataType parameterType) {
    this.parameterType = parameterType;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public void setAllowMultiple(Boolean allowMultiple) {
    this.allowMultiple = allowMultiple;
  }
}
