package com.mangofactory.swagger.readers.operation.parameter;

import com.google.common.base.Function;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.AllowableListValues;
import com.mangofactory.service.model.AllowableValues;
import com.mangofactory.springmvc.plugins.ParameterExpanderPlugin;
import com.mangofactory.springmvc.plugins.ParameterExpansionContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;

@Component
public class ParameterExpander implements ParameterExpanderPlugin {
  @Override
  public void apply(ParameterExpansionContext context) {
    AllowableValues allowable = allowableValues(context.getField());

    String name = isNullOrEmpty(context.getParentName()) ? context.getField().getName() : String.format("%s.%s",
            context.getParentName(),
            context.getField().getName());
    context.getParameterBuilder()
            .name(name)
            .description(null).defaultValue(null)
            .required(Boolean.FALSE)
            .allowMultiple(Boolean.FALSE)
            .dataType(context.getDataTypeName())
            .allowableValues(allowable)
            .parameterType("query")
            .parameterAccess(null);
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

  private AllowableValues allowableValues(final Field field) {

    AllowableValues allowable = null;
    if (field.getType().isEnum()) {
      allowable = new AllowableListValues(getEnumValues(field.getType()), "LIST");
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
