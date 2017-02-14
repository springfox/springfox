/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.swagger.annotations;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Lists.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

public class Annotations {

  private Annotations() {
    throw new UnsupportedOperationException();
  }

  public static Optional<ApiParam> findApiParamAnnotation(AnnotatedElement annotated) {
    return fromNullable(getAnnotation(annotated, ApiParam.class));
  }

  public static List<ApiResponses> findApiResponsesAnnotations(AnnotatedElement annotated) {
    List<ApiResponses> results = newArrayList();
    ApiResponses currentLevel = getAnnotation(annotated, ApiResponses.class);
    if (currentLevel != null) {
      results.add(currentLevel);
    }
    if (annotated instanceof Method) {
      ApiResponses parentLevel = findAnnotation(((Method)annotated).getDeclaringClass(), ApiResponses.class);
      if (parentLevel != null) {
        results.add(parentLevel);
      }
    }
    return results;
  }

  public static Function<ApiOperation, ResolvedType> resolvedTypeFromOperation(final TypeResolver typeResolver,
      final ResolvedType defaultType) {

    return new Function<ApiOperation, ResolvedType>() {
      @Override
      public ResolvedType apply(ApiOperation annotation) {
        return getResolvedType(annotation, typeResolver, defaultType);
      }
    };
  }

  public static Function<ApiResponse, ResolvedType> resolvedTypeFromResponse(final TypeResolver typeResolver,
      final ResolvedType defaultType) {

    return new Function<ApiResponse, ResolvedType>() {
      @Override
      public ResolvedType apply(ApiResponse annotation) {
        return getResolvedType(annotation, typeResolver, defaultType);
      }
    };
  }

  @VisibleForTesting
  static ResolvedType getResolvedType(ApiOperation annotation,
      TypeResolver typeResolver, ResolvedType defaultType) {

    if (null != annotation && Void.class != annotation.response()) {
      if ("List".compareToIgnoreCase(annotation.responseContainer()) == 0) {
        return typeResolver.resolve(List.class, annotation.response());
      } else if ("Set".compareToIgnoreCase(annotation.responseContainer()) == 0) {
        return typeResolver.resolve(Set.class, annotation.response());
      } else {
        return typeResolver.resolve(annotation.response());
      }
    }
    return defaultType;
  }

  @VisibleForTesting
  static ResolvedType getResolvedType(ApiResponse annotation,
      TypeResolver typeResolver, ResolvedType defaultType) {

    if (null != annotation && Void.class != annotation.response()) {
      if ("List".compareToIgnoreCase(annotation.responseContainer()) == 0) {
        return typeResolver.resolve(List.class, annotation.response());
      } else if ("Set".compareToIgnoreCase(annotation.responseContainer()) == 0) {
        return typeResolver.resolve(Set.class, annotation.response());
      } else {
        return typeResolver.resolve(annotation.response());
      }
    }
    return defaultType;
  }
}
