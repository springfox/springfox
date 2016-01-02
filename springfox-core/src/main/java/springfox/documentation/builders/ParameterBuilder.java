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
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.Parameter;

import static springfox.documentation.builders.BuilderDefaults.*;

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
  private ModelReference modelRef;


  /**
   * Copy builder
   *
   * @param other parameter to copy from
   * @return this
   */
  ParameterBuilder from(Parameter other) {
    return name(other.getName())
        .allowableValues(other.getAllowableValues())
        .allowMultiple(other.isAllowMultiple())
        .defaultValue(other.getDefaultValue())
        .description(other.getDescription())
        .modelRef(other.getModelRef())
        .parameterAccess(other.getParamAccess())
        .parameterType(other.getParamType())
        .required(other.isRequired())
        .type(other.getType().orNull());
  }

  /**
   * Updates the parameter name
   *
   * @param name - name of the parameter
   * @return this
   */
  public ParameterBuilder name(String name) {
    this.name = defaultIfAbsent(name, this.name);
    return this;
  }

  /**
   * Updates the description of the parameter
   *
   * @param description - description
   * @return this
   */
  public ParameterBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  /**
   * Updates the default value of the parametr
   *
   * @param defaultValue - default value
   * @return this
   */
  public ParameterBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultIfAbsent(defaultValue, this.defaultValue);
    return this;
  }

  /**
   * Updates if the parameter is required or optional
   *
   * @param required - flag to indicate if the parameter is required
   * @return this
   */
  public ParameterBuilder required(boolean required) {
    this.required = required;
    return this;
  }

  /**
   * Updates if the parameter should allow multiple values
   *
   * @param allowMultiple - flag to indicate if the parameter supports multi-value
   * @return this
   */
  public ParameterBuilder allowMultiple(boolean allowMultiple) {
    this.allowMultiple = allowMultiple;
    return this;
  }

  /**
   * Updates if the parameter is bound by a range of values or a range of numerical values
   *
   * @param allowableValues - allowable values (instance of @see springfox.documentation.service.AllowableListValues
   *                        or @see springfox.documentation.service.AllowableRangeValues)
   * @return
   */
  public ParameterBuilder allowableValues(AllowableValues allowableValues) {
    this.allowableValues = defaultIfAbsent(allowableValues, this.allowableValues);
    return this;
  }

  /**
   * Updates the type of parameter
   *
   * @param paramType - Could be header, cookie, body, query etc.
   * @return this
   */
  public ParameterBuilder parameterType(String paramType) {
    this.paramType = defaultIfAbsent(paramType, this.paramType);
    return this;
  }

  /**
   * Updates the parameter access
   *
   * @param paramAccess - parameter access
   * @return this
   */
  public ParameterBuilder parameterAccess(String paramAccess) {
    this.paramAccess = defaultIfAbsent(paramAccess, this.paramAccess);
    return this;
  }

  /**
   * Updates the type of parameter
   *
   * @param type - represents the resolved type of the parameter
   * @return this
   */
  public ParameterBuilder type(ResolvedType type) {
    this.type = defaultIfAbsent(type, this.type);
    return this;
  }

  /**
   * Represents the convenience method to infer the model reference
   * Consolidate or figure out whats can be rolled into the other.
   *
   * @param modelRef
   * @return
   */
  public ParameterBuilder modelRef(ModelReference modelRef) {
    this.modelRef = defaultIfAbsent(modelRef, this.modelRef);
    return this;
  }


  public Parameter build() {
    return new Parameter(
        name,
        description,
        defaultValue,
        required,
        allowMultiple,
        modelRef,
        Optional.fromNullable(type),
        allowableValues,
        paramType,
        paramAccess);
  }
}