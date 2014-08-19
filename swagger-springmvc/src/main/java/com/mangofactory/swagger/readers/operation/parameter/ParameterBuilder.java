package com.mangofactory.swagger.readers.operation.parameter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.Annotations;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.model.AllowableListValues;
import com.wordnik.swagger.model.AllowableValues;
import com.wordnik.swagger.model.Parameter;
import scala.collection.JavaConversions;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.ScalaUtils.*;
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

    return new Parameter(
            isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName()),
            toOption(null), //description
            toOption(null), //default value
            Boolean.FALSE,  //required
            Boolean.FALSE,  //allow multiple
            dataTypeName,   //data type
            allowable,           //allowable values
            "query",        //param type
            toOption(null)  //param access
    );

  }

  private Parameter fromApiParam(ApiParam apiParam) {
    String allowableProperty = emptyToNull(apiParam.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), field);

    return new Parameter(
            isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName()),
            toOption(apiParam.value()),
            toOption(apiParam.defaultValue()),
            apiParam.required(),
            apiParam.allowMultiple(),
            dataTypeName,
            allowable,
            "query", //param type
            toOption(apiParam.access()));
  }

  private Parameter fromApiModelProperty(ApiModelProperty apiModelProperty) {
    String allowableProperty = emptyToNull(apiModelProperty.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), field);
    return new Parameter(
            isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName()),
            toOption(apiModelProperty.value()),
            toOption(null), //default value
            apiModelProperty.required(),
            Boolean.FALSE,  //allow multiple
            dataTypeName,
            allowable,
            "query", //param type
            toOption(apiModelProperty.access()));
  }

  private AllowableValues allowableValues(final Optional<String> optionalAllowable, final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(JavaConversions.collectionAsScalaIterable(
              getEnumValues(field.getType())).toList(), "LIST");
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
