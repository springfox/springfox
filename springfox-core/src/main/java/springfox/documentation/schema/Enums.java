/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static java.util.Arrays.asList;

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
    return transformUnique(subject.getEnumConstants(), new Function<Object, String>() {
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
  @SuppressWarnings("PMD")
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

  private static <E> List<String> transformUnique(E[] values, Function<E, String> mapper) {
    List<String> nonUniqueValues = transform(asList(values), mapper);
    Set<String> uniqueValues = new LinkedHashSet<String>(nonUniqueValues);
    return new ArrayList<String>(uniqueValues);
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

  public static AllowableValues emptyListValuesToNull(AllowableListValues values) {
    if (!values.getValues().isEmpty()) {
      return values;
    }
    return null;
  }
}
