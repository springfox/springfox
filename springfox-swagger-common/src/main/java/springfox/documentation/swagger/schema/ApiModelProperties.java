/*
 *
 *  Copyright 2015-2017 the original author or authors.
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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.annotation.AnnotationUtils;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spring.web.DescriptionResolver;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.springframework.util.StringUtils.*;

public final class ApiModelProperties {

  private ApiModelProperties() {
    throw new UnsupportedOperationException();
  }

  static Function<ApiModelProperty, AllowableValues> toAllowableValues() {
    return new Function<ApiModelProperty, AllowableValues>() {
      @Override
      public AllowableValues apply(ApiModelProperty annotation) {
        return allowableValueFromString(annotation.allowableValues());
      }
    };
  }

  public static AllowableValues allowableValueFromString(String allowableValueString) {
    AllowableValues allowableValues = new AllowableListValues(Lists.<String>newArrayList(), "LIST");
    String trimmed = allowableValueString.trim();
    if (trimmed.startsWith("range[")) {
      trimmed = trimmed.replaceAll("range\\[", "").replaceAll("]", "");
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(trimmed);
      List<String> ranges = newArrayList(split);
      allowableValues = new AllowableRangeValues(ranges.get(0), ranges.get(1));
    } else if (trimmed.contains(",")) {
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(trimmed);
      allowableValues = new AllowableListValues(newArrayList(split), "LIST");
    } else if (hasText(trimmed)) {
      List<String> singleVal = Collections.singletonList(trimmed);
      allowableValues = new AllowableListValues(singleVal, "LIST");
    }
    return allowableValues;
  }

  static Function<ApiModelProperty, Boolean> toIsRequired() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.required();
      }
    };
  }

  static Function<ApiModelProperty, Integer> toPosition() {
    return new Function<ApiModelProperty, Integer>() {
      @Override
      public Integer apply(ApiModelProperty annotation) {
        return annotation.position();
      }
    };
  }

  static Function<ApiModelProperty, Boolean> toIsReadOnly() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.readOnly();
      }
    };
  }

  static Function<ApiModelProperty, String> toDescription(
      final DescriptionResolver descriptions) {
    
    return new Function<ApiModelProperty, String>() {
      @Override
      public String apply(ApiModelProperty annotation) {
        String description = "";
        if (!Strings.isNullOrEmpty(annotation.value())) {
          description = annotation.value();
        } else if (!Strings.isNullOrEmpty(annotation.notes())) {
          description = annotation.notes();
        }
        return descriptions.resolve(description);
      }
    };
  }

  static Function<ApiModelProperty, ResolvedType> toType(final TypeResolver resolver) {
    return new Function<ApiModelProperty, ResolvedType>() {
      @Override
      public ResolvedType apply(ApiModelProperty annotation) {
        try {
          return resolver.resolve(Class.forName(annotation.dataType()));
        } catch (ClassNotFoundException e) {
          return resolver.resolve(Object.class);
        }
      }
    };
  }

  public static Optional<ApiModelProperty> findApiModePropertyAnnotation(AnnotatedElement annotated) {
    return Optional.fromNullable(AnnotationUtils.getAnnotation(annotated, ApiModelProperty.class));
  }

  static Function<ApiModelProperty, Boolean> toHidden() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.hidden();
      }
    };
  }

  static Function<ApiModelProperty, String> toExample() {
    return new Function<ApiModelProperty, String>() {
      @Override
      public String apply(ApiModelProperty annotation) {
        String example = "";
        if (!Strings.isNullOrEmpty(annotation.example())) {
          example = annotation.example();
        }
        return example;
      }
    };
  }
}
