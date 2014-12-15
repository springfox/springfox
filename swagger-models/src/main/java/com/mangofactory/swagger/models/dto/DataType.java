package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//CHECKSTYLE:OFF CyclomaticComplexityCheck
public class DataType implements SwaggerDataType {
  @JsonUnwrapped
  @JsonProperty
  private final SwaggerDataType dataType;
  private static final Pattern containerPattern = Pattern.compile("([a-zA-Z]+)\\[([a-zA-Z\\.\\-]+)\\]");

  public DataType(String initialType) {
    this.dataType = typeFromDataType(initialType);
  }

  public SwaggerDataType typeFromDataType(String initialType) {

    if (isOfType(initialType, "void")) {
      return new PrimitiveDataType("void");
    }
    if (isOfType(initialType, "int")) {
      return new PrimitiveFormatDataType("integer", "int32");
    }
    if (isOfType(initialType, "long")) {
      return new PrimitiveFormatDataType("integer", "int64");
    }
    if (isOfType(initialType, "float")) {
      return new PrimitiveFormatDataType("integer", "int64");
    }
    if (isOfType(initialType, "double")) {
      return new PrimitiveFormatDataType("number", "double");
    }
    if (isOfType(initialType, "string")) {
      return new PrimitiveDataType("string");
    }
    if (isOfType(initialType, "byte")) {
      return new PrimitiveFormatDataType("string", "byte");
    }
    if (isOfType(initialType, "boolean")) {
      return new PrimitiveDataType("boolean");
    }
    if (isOfType(initialType, "Date") || isOfType(initialType, "DateTime")) {
      return new PrimitiveFormatDataType("string", "date-time");
    }
    if (isOfType(initialType, "BigDecimal") || isOfType(initialType, "BigInteger")) {
      return new PrimitiveDataType("number");
    }
    if (isOfType(initialType, "UUID")) {
      return new PrimitiveFormatDataType("string", "uuid");
    }
    if (isOfType(initialType, "date")) {
      return new PrimitiveFormatDataType("string", "date");
    }
    if (isOfType(initialType, "date-time")) {
      return new PrimitiveFormatDataType("string", "date-time");
    }
    Matcher matcher = containerPattern.matcher(initialType);
    if (matcher.matches()) {
      String containerInnerType = matcher.group(2);
      if (isUniqueContainerType(matcher.group(1))) {
        return new ContainerDataType(containerInnerType, true);
      } else {
        return new ContainerDataType(containerInnerType, false);
      }
    }

    return new ReferenceDataType(initialType);
  }

  private boolean isUniqueContainerType(String containerInnerType) {
    return null != containerInnerType && containerInnerType.equalsIgnoreCase("Set");
  }

  private boolean isOfType(String initialType, String ofType) {
    return initialType.equals(ofType);
  }

  @Override
  public String getAbsoluteType() {
    return dataType.getAbsoluteType();
  }
}
//CHECKSTYLE:ON