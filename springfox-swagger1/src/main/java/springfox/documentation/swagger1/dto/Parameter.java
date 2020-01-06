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

  @SuppressWarnings("ParameterNumber")
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
