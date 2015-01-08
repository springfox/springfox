package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.AllowableValues;
import com.mangofactory.service.model.Parameter;

public class ParameterBuilder {
  private String name;
  private String description;
  private String defaultValue;
  private boolean required;
  private boolean allowMultiple;
  private String dataType;
  private AllowableValues allowableValues;
  private String paramType;
  private String paramAccess;

  public ParameterBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ParameterBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ParameterBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public ParameterBuilder required(Boolean required) {
    this.required = required;
    return this;
  }

  public ParameterBuilder allowMultiple(Boolean allowMultiple) {
    this.allowMultiple = allowMultiple;
    return this;
  }

  public ParameterBuilder dataType(String dataType) {
    this.dataType = dataType;
    return this;
  }

  public ParameterBuilder allowableValues(AllowableValues allowableValues) {
    this.allowableValues = allowableValues;
    return this;
  }

  public ParameterBuilder parameterType(String paramType) {
    this.paramType = paramType;
    return this;
  }

  public ParameterBuilder parameterAccess(String paramAccess) {
    this.paramAccess = paramAccess;
    return this;
  }

  public Parameter build() {
    return new Parameter(name, description, defaultValue, required, allowMultiple, dataType, allowableValues,
            paramType, paramAccess);
  }
}