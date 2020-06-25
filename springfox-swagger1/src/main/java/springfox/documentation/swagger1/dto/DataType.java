/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CHECKSTYLE:OFF CyclomaticComplexityCheck
public class DataType implements SwaggerDataType {
  @SuppressWarnings("java:S4784")
  private static final Pattern containerPattern = Pattern.compile("([a-zA-Z]+)\\[([_a-zA-Z0-9\\.\\-]+)\\]");
  @JsonUnwrapped
  @JsonProperty
  private SwaggerDataType dataType;

  public DataType(String initialType) {
    this.dataType = typeFromDataType(initialType);
  }

  public SwaggerDataType typeFromDataType(String initialType) {

    if (isOfType(initialType.toLowerCase(), "void")) {
      return new PrimitiveDataType("void");
    }
    if (isOfType(initialType, "int")) {
      return new PrimitiveFormatDataType("integer", "int32");
    }
    if (isOfType(initialType, "long")) {
      return new PrimitiveFormatDataType("integer", "int64");
    }
    if (isOfType(initialType, "float")) {
      return new PrimitiveFormatDataType("number", "float");
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
    if (isOfType(initialType, "bigdecimal")) {
      return new PrimitiveDataType("number");
    }
    if (isOfType(initialType, "biginteger")) {
      return new PrimitiveDataType("integer");
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
    if (isOfType(initialType, "__file")) {
      return new PrimitiveDataType("File");
    }
    Matcher matcher = containerPattern.matcher(initialType);
    if (matcher.matches()) {
      String containerInnerType = matcher.group(2);
      if ("__file".equals(containerInnerType)) {
        containerInnerType = "File";
      }
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
