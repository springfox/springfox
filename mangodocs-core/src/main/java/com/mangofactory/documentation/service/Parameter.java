package com.mangofactory.documentation.service;

public class Parameter {
  private final String parameterType;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final Boolean required;
  private final Boolean allowMultiple;
  private final AllowableValues allowableValues;
  private final String paramType;
  private final String paramAccess;

  public Parameter(String name, String description, String defaultValue, boolean required, boolean allowMultiple,
                   String dataType, AllowableValues allowableValues, String paramType, String paramAccess) {
    this.description = description;
    this.defaultValue = defaultValue;
    this.required = required;
    this.allowMultiple = allowMultiple;
    this.allowableValues = allowableValues;
    this.paramType = paramType;
    this.paramAccess = paramAccess;
    this.name = name;
    this.parameterType = dataType;
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

  public String getParameterType() {
    return parameterType;
  }

}
