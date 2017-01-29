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

package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Objects;

import springfox.documentation.service.AllowableValues;

public class ModelProperty {
  private final String name;
  private final ResolvedType type;
  private final String qualifiedType;
  private final int position;
  private final Boolean required;
  private final boolean isHidden;
  private final Boolean readOnly;
  private final String description;
  private final AllowableValues allowableValues;
  private Function<ResolvedType, ? extends ModelReference> modelRefFactory;
  private final String example;
  private final String pattern;

  public ModelProperty(
      String name,
      ResolvedType type,
      String qualifiedType,
      int position,
      Boolean required,
      Boolean isHidden,
      Boolean readOnly,
      String description,
      AllowableValues allowableValues,
      String example,
      String pattern) {

    this.name = name;
    this.type = type;
    this.qualifiedType = qualifiedType;
    this.position = position;
    this.required = required;
    this.isHidden = isHidden;
    this.readOnly = readOnly;
    this.description = description;
    this.allowableValues = allowableValues;
    this.example = example;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public ResolvedType getType() {
    return type;
  }

  public String getQualifiedType() {
    return qualifiedType;
  }

  public int getPosition() {
    return position;
  }

  public Boolean isRequired() {
    return required;
  }

  public Boolean isReadOnly() {
    return readOnly;
  }

  public String getDescription() {
    return description;
  }

  public AllowableValues getAllowableValues() {
    return allowableValues;
  }

  public ModelReference getModelRef() {
    return modelRefFactory.apply(type);
  }

  public boolean isHidden() {
    return isHidden;
  }

  public ModelProperty updateModelRefFactory(Function<ResolvedType, ? extends ModelReference> modelRefFactory) {
    this.modelRefFactory = modelRefFactory;
    return this;
  }

  public String getExample() {
    return example;
  }

  public String getPattern() {
    return pattern;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(name, type, qualifiedType, position, required, isHidden, 
                            readOnly, description, allowableValues, getModelRef(), example, pattern);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ModelProperty that = (ModelProperty) o;
    
    return Objects.equal(name, that.name) &&
        Objects.equal(type, that.type) &&
        Objects.equal(qualifiedType, that.qualifiedType) &&
        Objects.equal(position, that.position) &&
        Objects.equal(required, that.required) &&
        Objects.equal(isHidden, that.isHidden) &&
        Objects.equal(readOnly, that.readOnly) &&
        Objects.equal(description, that.description) &&
        Objects.equal(allowableValues, that.allowableValues) &&
        Objects.equal(getModelRef(), that.getModelRef()) &&
        Objects.equal(example, that.example) &&
        Objects.equal(pattern, that.pattern);
  }
}
