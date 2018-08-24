/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import com.google.common.collect.Multimap;
import org.springframework.core.Ordered;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelReference;

import java.util.List;

public class Parameter implements Ordered {
  public static final int DEFAULT_PRECEDENCE = 0;
  
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
  private final Boolean hidden;
  private final String pattern;
  private final String collectionFormat;
  private final int order;
  private final Object scalarExample;
  private final Multimap<String, Example> examples;
  private final List<VendorExtension> vendorExtensions;
  private final Boolean allowEmptyValue;

  public Parameter(
      String name,
      String description,
      String defaultValue,
      boolean required,
      boolean allowMultiple,
      Boolean allowEmptyValue,
      ModelReference modelRef,
      Optional<ResolvedType> type,
      AllowableValues allowableValues,
      String paramType,
      String paramAccess,
      boolean hidden,
      String pattern,
      String collectionFormat,
      int order,
      Object scalarExample,
      Multimap<String, Example> examples,
      List<VendorExtension> vendorExtensions) {

    this.description = description;
    this.defaultValue = defaultValue;
    this.required = required;
    this.allowMultiple = allowMultiple;
    this.allowEmptyValue = allowEmptyValue;
    this.modelRef = modelRef;
    this.type = type;
    this.allowableValues = allowableValues;
    this.paramType = paramType;
    this.paramAccess = paramAccess;
    this.name = name;
    this.hidden = hidden;
    this.pattern = pattern;
    this.collectionFormat = collectionFormat;
    this.order = order;
    this.scalarExample = scalarExample;
    this.examples = examples;
    this.vendorExtensions = vendorExtensions;
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
  
  public Boolean isHidden() {
    return hidden;
  }

  public String getPattern() {
    return pattern;
  }

  public List<VendorExtension> getVendorExtentions() {
    return vendorExtensions;
  }

  public String getCollectionFormat() {
    return collectionFormat;
  }

  public Boolean isAllowEmptyValue() {
    return allowEmptyValue;
  }

  public Object getScalarExample() {
    return scalarExample;
  }

  public Multimap<String, Example> getExamples() {
    return examples;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Parameter{");
    sb.append("name='").append(name).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", order='").append(order).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
