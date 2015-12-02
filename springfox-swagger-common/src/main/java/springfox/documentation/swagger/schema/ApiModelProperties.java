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

package springfox.documentation.swagger.schema;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.util.StringUtils.hasText;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModelProperty;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableRangeValues;
import springfox.documentation.service.AllowableValues;

public final class ApiModelProperties {

  private ApiModelProperties() {
    throw new UnsupportedOperationException();
  }

  public static Function<ApiModelProperty, AllowableValues> toAllowableValues() {
    return new Function<ApiModelProperty, AllowableValues>() {
      @Override
      public AllowableValues apply(ApiModelProperty annotation) {
        return allowableValueFromString(annotation.allowableValues());
      }
    };
  }

  public static AllowableValues allowableValueFromString(String allowableValueString) {
    AllowableValues allowableValues = new AllowableListValues(Lists.<String>newArrayList(), "LIST");
    allowableValueString = allowableValueString.trim().replaceAll(" ", "");
    if (allowableValueString.startsWith("range[")) {
      allowableValueString = allowableValueString.replaceAll("range\\[", "").replaceAll("]", "");
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
      List<String> ranges = newArrayList(split);
      allowableValues = new AllowableRangeValues(ranges.get(0), ranges.get(1));
    } else if (allowableValueString.contains(",")) {
      Iterable<String> split = Splitter.on(',').trimResults().omitEmptyStrings().split(allowableValueString);
      allowableValues = new AllowableListValues(newArrayList(split), "LIST");
    } else if (hasText(allowableValueString)) {
      List<String> singleVal = Collections.singletonList(allowableValueString.trim());
      allowableValues = new AllowableListValues(singleVal, "LIST");
    }
    return allowableValues;
  }

  public static Function<ApiModelProperty, Boolean> toIsRequired() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.required();
      }
    };
  }

  public static Function<ApiModelProperty, Boolean> toIsReadOnly() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.readOnly();
      }
    };
  }

  public static Function<ApiModelProperty, String> toDescription() {
    return new Function<ApiModelProperty, String>() {
      @Override
      public String apply(ApiModelProperty annotation) {
        String description = "";
        if (!Strings.isNullOrEmpty(annotation.value())) {
          description = annotation.value();
        } else if (!Strings.isNullOrEmpty(annotation.notes())) {
          description = annotation.notes();
        }
        return description;
      }
    };
  }

  public static Function<ApiModelProperty, ResolvedType> toType(final TypeResolver resolver) {
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

  public static Function<ApiModelProperty, Boolean> toHidden() {
    return new Function<ApiModelProperty, Boolean>() {
      @Override
      public Boolean apply(ApiModelProperty annotation) {
        return annotation.hidden();
      }
    };
  }

  public static Function<ApiModelProperty, String> toExample() {
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
