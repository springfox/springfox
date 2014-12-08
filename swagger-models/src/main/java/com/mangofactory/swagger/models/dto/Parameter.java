package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Parameter {
  @JsonProperty
  @JsonUnwrapped
  private final ParameterType parameterType;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final Boolean required;
  private final Boolean allowMultiple;
  private final String dataType;
  @JsonProperty
  @JsonUnwrapped
  private final AllowableValues allowableValues;
  private final String paramType;
  private final String paramAccess;

  public Parameter(String name, String description, String defaultValue, Boolean required, Boolean allowMultiple,
                   String dataType, AllowableValues allowableValues, String paramType, String paramAccess) {
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
    this.required = required;
    this.allowMultiple = allowMultiple;
    this.dataType = dataType;
    this.allowableValues = allowableValues;
    this.paramType = paramType;
    this.paramAccess = paramAccess;
    this.parameterType = typeFromDataType();
  }

  private ParameterType typeFromDataType() {
    if (isOfType("int")) {
      return new PrimitiveFormatParameterType("integer", "int32");
    }
    if (isOfType("long")) {
      return new PrimitiveFormatParameterType("integer", "int64");
    }
    if (isOfType("float")) {
      return new PrimitiveFormatParameterType("integer", "int64");
    }
    if (isOfType("double")) {
      return new PrimitiveFormatParameterType("number", "double");
    }
    if (isOfType("string")) {
      return new PrimitiveParameterType("string");
    }
    if (isOfType("byte")) {
      return new PrimitiveFormatParameterType("string", "byte");
    }
    if (isOfType("boolean")) {
      return new PrimitiveParameterType("boolean");
    }
    if (isOfType("Date") || isOfType("DateTime")) {
      return new PrimitiveFormatParameterType("string", "date-time");
    }
    if (isOfType("BigDecimal") || isOfType("BigInteger")) {
      return new PrimitiveParameterType("number");
    }
    if (isOfType("UUID")) {
      return new PrimitiveFormatParameterType("string", "uuid");
    }
    if (isOfType("date")) {
      return new PrimitiveFormatParameterType("string", "date");
    }
    if (isOfType("date-time")) {
      return new PrimitiveFormatParameterType("string", "date-time");
    }
    return new PrimitiveParameterType(dataType);
  }

  private boolean isOfType(String ofType) {
    return dataType.equals(ofType);
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

  public String getDataType() {
    return dataType;
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

  public ParameterType getParameterType() {
    return parameterType;
  }
}
