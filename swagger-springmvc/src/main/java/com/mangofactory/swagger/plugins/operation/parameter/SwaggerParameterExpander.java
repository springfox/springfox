package com.mangofactory.swagger.plugins.operation.parameter;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.schema.Annotations;
import com.mangofactory.service.model.AllowableListValues;
import com.mangofactory.service.model.AllowableValues;
import com.mangofactory.springmvc.plugins.ParameterExpanderPlugin;
import com.mangofactory.springmvc.plugins.ParameterExpansionContext;
import com.mangofactory.swagger.plugins.ApiModelProperties;
import com.wordnik.swagger.annotations.ApiModelProperty;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static com.mangofactory.swagger.plugins.operation.parameter.ParameterAllowableReader.*;

@Component
public class SwaggerParameterExpander implements ParameterExpanderPlugin {

  @Override
  public void apply(ParameterExpansionContext context) {
    Optional<ApiModelProperty> apiModelPropertyOptional = ApiModelProperties.findApiModePropertyAnnotation
            (context.getField());
    if (apiModelPropertyOptional.isPresent()) {
      fromApiModelProperty(context, apiModelPropertyOptional.get());
    }
    Optional<ApiParam> apiParamOptional = Annotations.findApiParamAnnotation(context.getField());
    if (apiParamOptional.isPresent()) {
      fromApiParam(context, apiParamOptional.get());
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private void fromApiParam(ParameterExpansionContext context, ApiParam apiParam) {
    String allowableProperty = emptyToNull(apiParam.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), context.getField());
    String name = isNullOrEmpty(context.getParentName())
            ? context.getField().getName()
            : String.format("%s.%s",  context.getParentName(), context.getField().getName());
    context.getParameterBuilder()
            .name(name)
            .description(apiParam.value())
            .defaultValue(apiParam.defaultValue())
            .required(apiParam.required())
            .allowMultiple(apiParam.allowMultiple())
            .dataType(context.getDataTypeName())
            .allowableValues(allowable)
            .parameterType("query")
            .parameterAccess(apiParam.access())
            .build();
  }

  private void fromApiModelProperty(ParameterExpansionContext context, ApiModelProperty apiModelProperty) {
    String allowableProperty = emptyToNull(apiModelProperty.allowableValues());
    AllowableValues allowable = allowableValues(fromNullable(allowableProperty), context.getField());
    String name = isNullOrEmpty(context.getParentName()) ? context.getField().getName() : String.format("%s.%s",
            context.getParentName(),
            context.getField().getName());
    context.getParameterBuilder()
            .name(name)
            .description(apiModelProperty.value())
            .defaultValue(null)
            .required(apiModelProperty.required())
            .allowMultiple(Boolean.FALSE)
            .dataType(context.getDataTypeName())
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
