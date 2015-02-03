package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.model.AllowableValues;
import com.mangofactory.documentation.service.model.Parameter;
import org.springframework.util.StringUtils;

import static com.mangofactory.documentation.builders.BuilderDefaults.*;

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
    this.name = defaultIfAbsent(name, this.name);
    return this;
  }

  public ParameterBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  public ParameterBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultIfAbsent(defaultValue, this.defaultValue);
    return this;
  }

  public ParameterBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  public ParameterBuilder allowMultiple(boolean allowMultiple) {
    this.allowMultiple = allowMultiple;
    return this;
  }

  public ParameterBuilder dataType(String dataType) {
    this.dataType = defaultIfAbsent(dataType, this.dataType);
    return this;
  }

  public ParameterBuilder allowableValues(AllowableValues allowableValues) {
    this.allowableValues = defaultIfAbsent(allowableValues, this.allowableValues);
    return this;
  }

  public ParameterBuilder parameterType(String paramType) {
    this.paramType = defaultIfAbsent(paramType, this.paramType);
    return this;
  }

  public ParameterBuilder parameterAccess(String paramAccess) {
    this.paramAccess = defaultIfAbsent(paramAccess, this.paramAccess);
    return this;
  }

  //TODO: Whats the rule that needs this to be the case?
  private String maybeOverrideName(String aName) {
    if (StringUtils.hasText(this.paramType) && paramType.equals("body")) {
      return paramType;
    }
    return aName;
  }

  public Parameter build() {
    return new Parameter(maybeOverrideName(name), description, defaultValue, required, allowMultiple, dataType,
            allowableValues, paramType, paramAccess);
  }
}