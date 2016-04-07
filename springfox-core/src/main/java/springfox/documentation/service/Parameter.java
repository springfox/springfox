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

package springfox.documentation.service;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import springfox.documentation.schema.ModelReference;

public class Parameter {
  private final String name;
  private final String description;
  private final String defaultValue;
  private final Boolean required;
  private final Boolean allowMultiple;
  private final ModelReference modelRef;
  private final Optional<ResolvedType> type;
  private final AllowableValues allowableValues;
  private final String paramType;
  private final String paramAccess;

  public Parameter(
      String name,
      String description,
      String defaultValue,
      boolean required,
      boolean allowMultiple,
      ModelReference modelRef,
      Optional<ResolvedType> type,
      AllowableValues allowableValues,
      String paramType,
      String paramAccess) {

    this.description = description;
    this.defaultValue = defaultValue;
    this.required = required;
    this.allowMultiple = allowMultiple;
    this.modelRef = modelRef;
    this.type = type;
    this.allowableValues = allowableValues;
    this.paramType = paramType;
    this.paramAccess = paramAccess;
    this.name = name;
  }

  public Optional<ResolvedType> getType() {
    return type;
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

  public ModelReference getModelRef() {
    return modelRef;
  }
}
