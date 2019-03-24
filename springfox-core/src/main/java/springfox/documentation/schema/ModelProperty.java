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
import springfox.documentation.service.VendorExtension;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.*;


//CHECKSTYLE:OFF CyclomaticComplexityCheck
public class ModelProperty {
  private final String name;
  private final ResolvedType type;
  private final String qualifiedType;
  private final int position;
  private final Boolean required;
  private final boolean isHidden;
  private final Boolean readOnly;
  private final Boolean allowEmptyValue;
  private final String description;
  private final AllowableValues allowableValues;
  private ModelReference modelRef;
  private final Object example;
  private final String pattern;
  private final String defaultValue;
  private final Xml xml;
  private final List<VendorExtension> vendorExtensions;

  public ModelProperty(
      String name,
      ResolvedType type,
      String qualifiedType,
      int position,
      boolean required,
      boolean isHidden,
      boolean readOnly,
      Boolean allowEmptyValue,
      String description,
      AllowableValues allowableValues,
      Object example,
      String pattern,
      String defaultValue,
      Xml xml,
      List<VendorExtension> vendorExtensions) {

    this.name = name;
    this.type = type;
    this.qualifiedType = qualifiedType;
    this.position = position;
    this.required = required;
    this.isHidden = isHidden;
    this.readOnly = readOnly;
    this.allowEmptyValue = allowEmptyValue;
    this.description = description;
    this.allowableValues = allowableValues;
    this.example = example;
    this.pattern = pattern;
    this.defaultValue = defaultValue;
    this.xml = xml;
    this.vendorExtensions = newArrayList(vendorExtensions);
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
    return modelRef;
  }

  public boolean isHidden() {
    return isHidden;
  }

  public ModelProperty updateModelRef(Function<ResolvedType, ? extends ModelReference> modelRefFactory) {
    modelRef = modelRefFactory.apply(type);
    return this;
  }

  public Object getExample() {
    return example;
  }

  public String getPattern() {
    return pattern;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public Xml getXml() {
    return xml;
  }

  /***
   * Support for isAllowEmpty value
   * @return true if supported
   * @since 2.8.0
   */
  public Boolean isAllowEmptyValue() {
    return allowEmptyValue;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        name,
        type,
        qualifiedType,
        position,
        required,
        isHidden,
        readOnly,
        allowableValues,
        description,
        allowableValues,
        modelRef,
        example,
        pattern,
        defaultValue,
        xml,
        Collections.unmodifiableList(vendorExtensions));
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
        Objects.equal(modelRef, that.modelRef) &&
        Objects.equal(example, that.example) &&
        Objects.equal(pattern, that.pattern) &&
        Objects.equal(xml, that.xml) &&
        Objects.equal(allowEmptyValue, that.allowEmptyValue) &&
        Objects.equal(defaultValue, that.defaultValue) &&
        Objects.equal(vendorExtensions, that.vendorExtensions);
  }
}
//CHECKSTYLE:ON
