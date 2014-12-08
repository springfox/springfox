package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.dto.AllowableListValues;
import com.mangofactory.swagger.models.dto.AllowableValues;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;

public class Enums {

  private Enums() {
    throw new UnsupportedOperationException();
  }

  public static AllowableValues allowableValues(Class<?> type) {
    if (type.isEnum()) {
      List<String> enumValues = getEnumValues(type);
      return new AllowableListValues(enumValues, "LIST");
    }
    return null;
  }

  static List<String> getEnumValues(final Class<?> subject) {
    return transform(Arrays.asList(subject.getEnumConstants()), new Function<Object, String>() {
      @Override
      public String apply(Object input) {
        Optional<String> jsonValue = findJsonValueAnnotatedMethod(input)
                .transform(evaluateJsonValue(input));
        if (jsonValue.isPresent() && !isNullOrEmpty(jsonValue.get())) {
          return jsonValue.get();
        }
        return input.toString();
      }
    });
  }

  private static Function<Method, String> evaluateJsonValue(final Object enumConstant) {
    return new Function<Method, String>() {
      @Override
      public String apply(Method input) {
        try {
            return input.invoke(enumConstant).toString();
        } catch (Exception ignored) {
        }
        return "";
      }
    };
  }

  private static Optional<Method> findJsonValueAnnotatedMethod(Object enumConstant) {
    for (Method each : enumConstant.getClass().getMethods()) {
      JsonValue jsonValue = AnnotationUtils.findAnnotation(each, JsonValue.class);
      if (jsonValue != null && jsonValue.value()) {
        return Optional.of(each);
      }
    }
    return Optional.absent();
  }
}
