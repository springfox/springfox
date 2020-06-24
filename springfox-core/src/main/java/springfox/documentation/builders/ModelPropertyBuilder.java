/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import springfox.documentation.schema.Xml;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.builders.BuilderDefaults.*;


@SuppressWarnings("rawtypes")
public class ModelPropertyBuilder {
  private ResolvedType type;
  private String qualifiedType;
  private int position;
  private Boolean required;
  private Boolean readOnly;
  private String description;
  private AllowableValues allowableValues;
  private String name;
  private boolean isHidden;
  private Object example;
  private String pattern;
  private String defaultValue;
  private Xml xml;
  private Boolean allowEmptyValue;
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  public ModelPropertyBuilder() {
  }

  @SuppressWarnings("deprecation")
  public ModelPropertyBuilder(springfox.documentation.schema.ModelProperty modelProperty) {
    this.type = modelProperty.getType();
    this.qualifiedType = modelProperty.getQualifiedType();
    this.position = modelProperty.getPosition();
    this.required = modelProperty.isRequired();
    this.readOnly = modelProperty.isReadOnly();
    this.description = modelProperty.getDescription();
    this.allowableValues = modelProperty.getAllowableValues();
    this.name = modelProperty.getName();
    this.isHidden = modelProperty.isHidden();
    this.example = modelProperty.getExample();
    this.pattern = modelProperty.getPattern();
    this.defaultValue = modelProperty.getDefaultValue();
    this.xml = modelProperty.getXml();
    this.allowEmptyValue = modelProperty.isAllowEmptyValue();
    this.vendorExtensions.addAll(modelProperty.getVendorExtensions());
  }

  public ModelPropertyBuilder name(String name) {
    this.name = defaultIfAbsent(name, this.name);
    return this;
  }

  public ModelPropertyBuilder type(ResolvedType type) {
    this.type = replaceIfMoreSpecific(type, this.type);
    return this;
  }

  public ModelPropertyBuilder qualifiedType(String qualifiedType) {
    this.qualifiedType = defaultIfAbsent(qualifiedType, this.qualifiedType);
    return this;
  }

  public ModelPropertyBuilder position(int position) {
    this.position = position;
    return this;
  }

  public ModelPropertyBuilder required(Boolean required) {
    this.required = required;
    return this;
  }

  public ModelPropertyBuilder readOnly(Boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  public ModelPropertyBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
    return this;
  }

  /**
   * Updates the example
   * @param example - example value
   * @return this
   * @since 2.8.1
   */
  public ModelPropertyBuilder example(Object example) {
    this.example = defaultIfAbsent(example, this.example);
    return this;
  }

  public ModelPropertyBuilder allowableValues(AllowableValues allowableValues) {
    this.allowableValues = BuilderDefaults.emptyToNull(allowableValues, this.allowableValues);
    return this;
  }

  public ModelPropertyBuilder isHidden(Boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  public ModelPropertyBuilder pattern(String pattern) {
    this.pattern = defaultIfAbsent(pattern, this.pattern);
    return this;
  }

  public ModelPropertyBuilder extensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(nullToEmptyList(extensions));
    return this;
  }

  public ModelPropertyBuilder defaultValue(String defaultValue) {
    this.defaultValue = defaultIfAbsent(defaultValue, this.defaultValue);
    return this;
  }
  
  /***
   * Support for isAllowEmpty value
   * @param allowEmptyValue true or false
   * @return true if supported
   * @since 2.8.0
   */
  public ModelPropertyBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = allowEmptyValue;
    return this;
  }

  public ModelPropertyBuilder xml(Xml xml) {
    this.xml = defaultIfAbsent(xml, this.xml);
    return this;
  }

  @SuppressWarnings("deprecation")
  public springfox.documentation.schema.ModelProperty build() {
    if (xml != null && isEmpty(xml.getName())) {
      xml.setName(name);
    }
    return new springfox.documentation.schema.ModelProperty(
        name,
        type,
        qualifiedType,
        position,
        required == null ? false : required,
        isHidden,
        readOnly == null ? false : readOnly,
        allowEmptyValue,
        description,
        allowableValues,
        example,
        pattern,
        defaultValue,
        xml,
        vendorExtensions
    );
  }
}
