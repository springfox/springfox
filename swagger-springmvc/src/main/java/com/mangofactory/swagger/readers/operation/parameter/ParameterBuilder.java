package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.Annotations;
import com.mangofactory.swagger.models.SimpleProperties;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.models.parameters.Parameter;
import com.wordnik.swagger.models.parameters.QueryParameter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.transform;
import static com.mangofactory.swagger.models.ResolvedTypes.asResolved;

class ParameterBuilder {
  private String parentName;
  private Field field;
  private TypeResolver typeResolver = new TypeResolver();

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
//    AllowableValues allowable = allowableValues(Optional.<String>absent(), field);
    QueryParameter queryParameter = new QueryParameter();
    queryParameter.setName(parameterName());
    queryParameter.setDescription(null);
    queryParameter.setRequired(false);
    ResolvedType resolvedType = asResolved(typeResolver, field.getType());
    queryParameter.setProperty(SimpleProperties.fromType(resolvedType));
    return queryParameter;
  }

  private String parameterName() {
    return isNullOrEmpty(parentName) ? field.getName() : String.format("%s.%s", parentName, field.getName());
  }

  private Parameter fromApiParam(ApiParam apiParam) {
    String allowableProperty = emptyToNull(apiParam.allowableValues());
//    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), field);
    QueryParameter queryParameter = new QueryParameter();
    queryParameter.setName(parameterName());
    queryParameter.setDescription(apiParam.value());
    queryParameter.setRequired(apiParam.required());
    queryParameter.setArray(apiParam.allowMultiple());
    queryParameter.setAccess(apiParam.access());
    queryParameter.setDefaultValue(apiParam.defaultValue());
    ResolvedType resolvedType = asResolved(typeResolver, field.getType());
    queryParameter.setProperty(SimpleProperties.fromType(resolvedType));
    return queryParameter;
  }

  private Parameter fromApiModelProperty(ApiModelProperty apiModelProperty) {
    String allowableProperty = emptyToNull(apiModelProperty.allowableValues());
//    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), field);
    QueryParameter queryParameter = new QueryParameter();
    queryParameter.setName(parameterName());
    queryParameter.setDescription(apiModelProperty.value());
    queryParameter.setRequired(apiModelProperty.required());
    queryParameter.setArray(false);
    queryParameter.setAccess(apiModelProperty.access());
    ResolvedType resolvedType = asResolved(typeResolver, field.getType());
    queryParameter.setProperty(SimpleProperties.fromType(resolvedType));
    return queryParameter;

  }

//  private AllowableValues allowableValues(final Optional<String> optionalAllowable, final Field field) {
//    AllowableValues allowable = null;
//    if (field.getType().isEnum()) {
//      allowable = new AllowableListValues(JavaConversions.collectionAsScalaIterable(
//              getEnumValues(field.getType())).toList(), "LIST");
//    } else if (optionalAllowable.isPresent()) {
//      allowable = allowableValueFromString(optionalAllowable.get());
//    }
//
//    return allowable;
//  }

  private List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(final Object input) {
        return input.toString();
      }
    });
  }
}
