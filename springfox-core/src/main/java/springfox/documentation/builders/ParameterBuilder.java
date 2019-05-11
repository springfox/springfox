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
import org.springframework.core.Ordered;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static springfox.documentation.builders.BuilderDefaults.*;

public class ParameterBuilder {
  private static final Collection<ParameterType> PARAMETER_TYPES_ALLOWING_EMPTY_VALUE =
      Arrays.asList(ParameterType.QUERY, ParameterType.FORMDATA);
  private String name;
  private String description;
  private String defaultValue;
  private boolean required;
  private boolean allowMultiple;
  private AllowableValues allowableValues;
  private ParameterType paramType;
  private String paramAccess;
  private ResolvedType type;
  private ModelReference modelRef;
  private boolean hidden;
  private String pattern;
  private List<VendorExtension> vendorExtensions = new ArrayList<>();
  private String collectionFormat = null;
  private Boolean allowEmptyValue;
  private int order = Ordered.LOWEST_PRECEDENCE;
  private Object scalarExample;
  private Map<String, List<Example>> examples = new HashMap<>();
  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;

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
        .type(other.getType().orElse(null))
        .hidden(other.isHidden())
        .allowEmptyValue(other.isAllowEmptyValue())
        .order(other.getOrder())
        .vendorExtensions(other.getVendorExtentions())
        .collectionFormat(other.getCollectionFormat());
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
   * Updates the default value of the parameter
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
   * @return this
   */
  public ParameterBuilder allowableValues(AllowableValues allowableValues) {
    this.allowableValues = emptyToNull(allowableValues, this.allowableValues);
    return this;
  }

  /**
   * Updates the type of parameter
   * @deprecated @since 3.0.0. Use @see {@link ParameterBuilder#parameterType(ParameterType)} instead
   * @param paramType - Could be header, cookie, body, query etc.
   * @return this
   */
  @Deprecated
  public ParameterBuilder parameterType(String paramType) {
    if (paramType != null && paramType.length() > 0) {
      this.paramType = defaultIfAbsent(ParameterType.valueOf(paramType.toUpperCase()), this.paramType);
    }
    return this;
  }

  /**
   * Updates the type of parameter
   * @since 3.0.0
   * @param paramType - Could be header, cookie, body, query etc.
   * @return this
   */
  public ParameterBuilder parameterType(ParameterType paramType) {
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
   * @param modelRef model reference
   * @return this
   */
  public ParameterBuilder modelRef(ModelReference modelRef) {
    this.modelRef = defaultIfAbsent(modelRef, this.modelRef);
    return this;
  }

  /**
   * Updates if the parameter is hidden
   *
   * @param hidden - flag to indicate if the parameter is hidden
   * @return this
   */
  public ParameterBuilder hidden(boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  /**
   * Updates the parameter extensions
   *
   * @param extensions - parameter extensions
   * @return this
   */
  public ParameterBuilder vendorExtensions(List<VendorExtension> extensions) {
    this.vendorExtensions.addAll(nullToEmptyList(extensions));
    return this;
  }

  /**
   * Updates the parameter extensions
   *
   * @param collectionFormat - parameter collection format
   * @return this
   * @since 2.8.0
   */
  public ParameterBuilder collectionFormat(String collectionFormat) {
    this.collectionFormat = defaultIfAbsent(collectionFormat, this.collectionFormat);
    return this;
  }

  /**
   * Updates the flag that allows sending empty values for this parameter
   * @param allowEmptyValue - true/false
   * @return this
   * @since 2.8.1
   */
  public ParameterBuilder allowEmptyValue(Boolean allowEmptyValue) {
    this.allowEmptyValue = defaultIfAbsent(allowEmptyValue, this.allowEmptyValue);
    return this;
  }

  /**
   * Updates default order of precedence of parameters
   * @param order - between {@link Ordered#HIGHEST_PRECEDENCE}, {@link Ordered#LOWEST_PRECEDENCE}
   * @return this
   * @since 2.8.1
   */
  public ParameterBuilder order(int order) {
    this.order = order;
    return this;
  }

  public ParameterBuilder pattern(String pattern) {
    this.pattern = defaultIfAbsent(pattern, this.pattern);
    return this;
  }

  /**
   * @since 2.8.1
   * @param scalarExample example for non-body parameters
   * @return this
   */
  public ParameterBuilder scalarExample(Object scalarExample) {
    this.scalarExample = defaultIfAbsent(scalarExample, this.scalarExample);
    return this;
  }
  /**
   * @since 2.8.1
   * @param examples example for body parameters
   * @return this
   */
  public ParameterBuilder complexExamples(Map<String, List<Example>> examples) {
    this.examples.putAll(examples);
    return this;
  }


  /**
   * @since 3.0.0
   * @param style https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#style-values
   * @return this
   */
  public ParameterBuilder style(ParameterStyle style) {
    this.style = style;
    return this;
  }

  /**
   * @since 3.0.0
   * @param explode When this is true, parameter values of type array or object generate separate parameters
   *                for each value of the array or key-value pair of the map. For other types of parameters
   *                this property has no effect. When style is form, the default value is true. For all other
   *                styles, the default value is false.
   * @return this
   */
  public ParameterBuilder explode(Boolean explode) {
    this.explode = explode;
    return this;
  }

  /**
   * @since 3.0.0
   * @param allowReserved Determines whether the parameter value SHOULD allow reserved characters, as defined
   *                     by RFC3986 :/?#[]@!$&'()*+,;= to be included without percent-encoding. This property
   *                      only applies to parameters with an in value of query. The default value is false.
   * @return this
   */
  public ParameterBuilder allowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  public Parameter build() {
    if (!PARAMETER_TYPES_ALLOWING_EMPTY_VALUE.contains(paramType)) {
      allowEmptyValue = null;
    }
    return new Parameter(
        name,
        description,
        defaultValue,
        required,
        allowMultiple,
        allowEmptyValue,
        modelRef,
        type,
        allowableValues,
        paramType,
        paramAccess,
        hidden,
        pattern,
        collectionFormat,
        order,
        scalarExample,
        examples,
        vendorExtensions,
        style,
        explode,
        allowReserved);
  }
}
