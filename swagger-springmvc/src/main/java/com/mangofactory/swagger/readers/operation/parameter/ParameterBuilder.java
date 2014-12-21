package com.mangofactory.swagger.readers.operation.parameter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.swagger.schema.Annotations;
import com.mangofactory.servicemodel.AllowableListValues;
import com.mangofactory.servicemodel.AllowableValues;
import com.mangofactory.servicemodel.Parameter;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiParam;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.readers.operation.parameter.ParameterAllowableReader.*;

class ParameterBuilder {

  private String dataTypeName;
  private String parentName;
  private Field field;

  public ParameterBuilder withDataTypeName(String dataTypeName) {
    this.dataTypeName = dataTypeName;
    return this;
  }

  public ParameterBuilder withParentName(String parentName) {
    this.parentName = parentName;
    return this;
  }

  public ParameterBuilder forField(Field field) {
    this.field = field;
    return this;
  }

  public Parameter build() {
    Optional<ApiModelProperty> apiModelPropertyOptional = Annotations.findApiModePropertyAnnotation(field);
    if (apiModelPropertyOptional.isPresent()) {
      return fromApiModelProperty(apiModelPropertyOptional.get());
    }
    Optional<ApiParam> apiParamOptional = Annotations.findApiParamAnnotation(field);
    if (apiParamOptional.isPresent()) {
      return fromApiParam(apiParamOptional.get());
    }
    return defaultParameter();
  }

  private Parameter defaultParameter() {
    AllowableValues allowable = allowableValues(Optional.<String>absent(), field);

    return new com.mangofactory.servicemodel.builder.ParameterBuilder()
            .name(isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName()))
            .description(null).defaultValue(null)
            .required(Boolean.FALSE)
            .allowMultiple(Boolean.FALSE)
            .dataType(dataTypeName)
            .allowableValues(allowable)
            .parameterType("query")
            .parameterAccess(null)
            .build();

  }

  private Parameter fromApiParam(ApiParam apiParam) {
    String allowableProperty = emptyToNull(apiParam.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), field);

    return new com.mangofactory.servicemodel.builder.ParameterBuilder()
            .name(isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName()))
            .description(apiParam.value())
            .defaultValue(apiParam.defaultValue())
            .required(apiParam.required())
            .allowMultiple(apiParam.allowMultiple())
            .dataType(dataTypeName)
            .allowableValues(allowable)
            .parameterType("query")
            .parameterAccess(apiParam.access())
            .build();
  }

  private Parameter fromApiModelProperty(ApiModelProperty apiModelProperty) {
    String allowableProperty = emptyToNull(apiModelProperty.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), field);
    return new com.mangofactory.servicemodel.builder.ParameterBuilder()
            .name(isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName()))
            .description(apiModelProperty.value())
            .defaultValue(null)
            .required(apiModelProperty.required())
            .allowMultiple(Boolean.FALSE)
            .dataType(dataTypeName)
            .allowableValues(allowable).parameterType("query")
            .parameterAccess(apiModelProperty.access())
            .build();
  }

  private AllowableValues allowableValues(final Optional<String> optionalAllowable, final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(getEnumValues(field.getType()), "LIST");
    } else if (optionalAllowable.isPresent()) {
      allowable = allowableValueFromString(optionalAllowable.get());
    }

    return allowable;
  }

  private List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(final Object input) {
        return input.toString();
      }
    });
  }
}
