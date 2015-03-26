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

package springfox.documentation.builders;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import org.springframework.util.StringUtils;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.Parameter;

public class ParameterBuilder {
  private String name;
  private String description;
  private String defaultValue;
  private boolean required;
  private boolean allowMultiple;
  private AllowableValues allowableValues;
  private String paramType;
  private String paramAccess;
  private ResolvedType type;
  private ModelRef modelRef;

  public ParameterBuilder name(String name) {
    this.name = BuilderDefaults.defaultIfAbsent(name, this.name);
    return this;
  }

  public ParameterBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public ParameterBuilder defaultValue(String defaultValue) {
    this.defaultValue = BuilderDefaults.defaultIfAbsent(defaultValue, this.defaultValue);
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

  public ParameterBuilder allowableValues(AllowableValues allowableValues) {
    this.allowableValues = BuilderDefaults.defaultIfAbsent(allowableValues, this.allowableValues);
    return this;
  }

  public ParameterBuilder parameterType(String paramType) {
    this.paramType = BuilderDefaults.defaultIfAbsent(paramType, this.paramType);
    return this;
  }

  public ParameterBuilder parameterAccess(String paramAccess) {
    this.paramAccess = BuilderDefaults.defaultIfAbsent(paramAccess, this.paramAccess);
    return this;
  }

  public ParameterBuilder type(ResolvedType type) {
    this.type = BuilderDefaults.defaultIfAbsent(type, this.type);
    return this;
  }

  public ParameterBuilder modelRef(ModelRef modelRef) {
    this.modelRef = BuilderDefaults.defaultIfAbsent(modelRef, this.modelRef);
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
    return new Parameter(maybeOverrideName(name), description, defaultValue, required, allowMultiple,
            modelRef, Optional.fromNullable(type), allowableValues, paramType, paramAccess);
  }
}