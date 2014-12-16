package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import static org.springframework.util.StringUtils.*;

public class Parameter {
  @JsonProperty
  @JsonUnwrapped
  private final SwaggerDataType parameterType;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final Boolean required;
  private final Boolean allowMultiple;
  @JsonProperty
  @JsonUnwrapped
  private final AllowableValues allowableValues;
  private final String paramType;
  private final String paramAccess;

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
    if (hasText(this.paramType) && paramType.equals("body")) {
      return paramType;
    }
    return aName;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getDefaultValue() {
    return defaultValue;
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

  public String getParamType() {
    return paramType;
  }

  public String getParamAccess() {
    return paramAccess;
  }

  public SwaggerDataType getParameterType() {
    return parameterType;
  }
}
