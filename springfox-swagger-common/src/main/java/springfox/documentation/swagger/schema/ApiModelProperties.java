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

package springfox.documentation.swagger.schema;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spring.web.DescriptionResolver;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;

public final class ApiModelProperties {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApiModelProperties.class);
  @SuppressWarnings("java:S4784")
  private static final Pattern RANGE_PATTERN = Pattern.compile("range([\\[(])(.*),(.*)([])])$");

  private ApiModelProperties() {
    throw new UnsupportedOperationException();
  }

  static Function<ApiModelProperty, AllowableValues> toAllowableValues() {
    return annotation -> allowableValueFromString(annotation.allowableValues());
  }

  public static AllowableValues allowableValueFromString(String allowableValueString) {
    AllowableValues allowableValues = new AllowableListValues(new ArrayList<String>(), "LIST");
    String trimmed = allowableValueString.trim();
    Matcher matcher = RANGE_PATTERN.matcher(trimmed.replaceAll(" ", ""));
    if (matcher.matches()) {
      if (matcher.groupCount() != 4) {
        LOGGER.warn("Unable to parse range specified {} correctly", trimmed);
      } else {
        allowableValues = new AllowableRangeValues(
            matcher.group(2).contains("infinity") ? null : matcher.group(2),
            matcher.group(1).equals("("),
            matcher.group(3).contains("infinity") ? null : matcher.group(3),
            matcher.group(4).equals(")"));
      }
    } else if (trimmed.contains(",")) {
      List<String> split =
          Stream.of(trimmed.split(",")).map(String::trim).filter(item -> !item.isEmpty()).collect(toList());
      allowableValues = new AllowableListValues(split, "LIST");
    } else if (hasText(trimmed)) {
      List<String> singleVal = singletonList(trimmed);
      allowableValues = new AllowableListValues(singleVal, "LIST");
    }
    return allowableValues;
  }

  static Function<ApiModelProperty, String> toDescription(
      final DescriptionResolver descriptions) {

    return annotation -> {
      String description = "";
      if (!isEmpty(annotation.value())) {
        description = annotation.value();
      } else if (!isEmpty(annotation.notes())) {
        description = annotation.notes();
      }
      return descriptions.resolve(description);
    };
  }

  static Function<ApiModelProperty, ResolvedType> toType(final TypeResolver resolver) {
    return annotation -> {
      try {
        return resolver.resolve(Class.forName(annotation.dataType()));
      } catch (ClassNotFoundException e) {
        return resolver.resolve(Object.class);
      }
    };
  }

  public static Optional<ApiModelProperty> findApiModePropertyAnnotation(AnnotatedElement annotated) {
    Optional<ApiModelProperty> annotation = empty();

    if (annotated instanceof Method) {
      // If the annotated element is a method we can use this information to check superclasses as well
      annotation = ofNullable(AnnotationUtils.findAnnotation(((Method) annotated), ApiModelProperty.class));
    }

    return annotation.map(Optional::of).orElse(ofNullable(AnnotationUtils.getAnnotation(annotated,
        ApiModelProperty.class)));
  }


  static Function<ApiModelProperty, String> toExample() {
    return annotation -> {
      String example = "";
      if (!isEmpty(annotation.example())) {
        example = annotation.example();
      }
      return example;
    };
  }
}
