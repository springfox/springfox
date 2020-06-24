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
import org.springframework.core.Ordered;
import springfox.documentation.schema.Example;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * @deprecated @since 3.0.0 Use @see {@link RequestParameter}, {@link RequestBody}
 */
@Deprecated
public class Parameter implements Ordered {

  private final String name;
  private final String description;
  private final String defaultValue;
  private final Boolean required;
  private final Boolean allowMultiple;
  private final springfox.documentation.schema.ModelReference modelRef;
  private final ResolvedType type;
  private final AllowableValues allowableValues;
  private final ParameterType paramType;
  private final String paramAccess;
  private final Boolean hidden;
  private final String pattern;
  private final String collectionFormat;
  private final int order;
  private final Object scalarExample;
  private final Map<String, List<Example>> examples;
  private final List<VendorExtension> vendorExtensions;
  private final Boolean allowEmptyValue;
  private final ParameterStyle style;
  private final Boolean explode;
  private final Boolean allowReserved;
  
  @SuppressWarnings("ParameterNumber")
  public Parameter(
      String name,
      String description,
      String defaultValue,
      boolean required,
      boolean allowMultiple,
      Boolean allowEmptyValue,
      springfox.documentation.schema.ModelReference modelRef,
      ResolvedType type,
      AllowableValues allowableValues,
      ParameterType paramType,
      String paramAccess,
      boolean hidden,
      String pattern,
      String collectionFormat,
      int order,
      Object scalarExample,
      Map<String, List<Example>> examples,
      List<VendorExtension> vendorExtensions,
      ParameterStyle style,
      Boolean explode,
      Boolean allowReserved) {

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
    this.style = style;
    this.explode = explode;
    this.allowReserved = allowReserved;
  }

  public Optional<ResolvedType> getType() {
    return Optional.ofNullable(type);
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
    if (paramType != null) {
      return paramType.getIn();
    }
    return null;
  }

  public Boolean getRequired() {
    return required;
  }

  public Boolean getAllowMultiple() {
    return allowMultiple;
  }

  public Boolean getHidden() {
    return hidden;
  }

  public List<VendorExtension> getVendorExtensions() {
    return vendorExtensions;
  }

  public ParameterStyle getStyle() {
    return style;
  }

  public Boolean getExplode() {
    return explode;
  }

  public Boolean getAllowReserved() {
    return allowReserved;
  }

  public String getParamAccess() {
    return paramAccess;
  }

  public springfox.documentation.schema.ModelReference getModelRef() {
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

  public Map<String, List<Example>> getExamples() {
    return examples;
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Parameter.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("description='" + description + "'")
        .add("defaultValue='" + defaultValue + "'")
        .add("required=" + required)
        .add("allowMultiple=" + allowMultiple)
        .add("modelRef=" + modelRef)
        .add("type=" + type)
        .add("allowableValues=" + allowableValues)
        .add("paramType=" + paramType)
        .add("paramAccess='" + paramAccess + "'")
        .add("hidden=" + hidden)
        .add("pattern='" + pattern + "'")
        .add("collectionFormat='" + collectionFormat + "'")
        .add("order=" + order)
        .add("scalarExample=" + scalarExample)
        .add("examples=" + examples)
        .add("vendorExtensions=" + vendorExtensions)
        .add("allowEmptyValue=" + allowEmptyValue)
        .add("style=" + style)
        .add("explode=" + explode)
        .add("allowReserved=" + allowReserved)
        .toString();
  }
}
