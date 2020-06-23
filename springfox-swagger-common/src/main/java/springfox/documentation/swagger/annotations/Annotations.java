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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.Optional.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

public class Annotations {

  private Annotations() {
    throw new UnsupportedOperationException();
  }

  public static Optional<ApiParam> findApiParamAnnotation(AnnotatedElement annotated) {
    return ofNullable(getAnnotation(annotated, ApiParam.class));
  }

  public static List<ApiResponses> findApiResponsesAnnotations(AnnotatedElement annotated) {
    List<ApiResponses> results = new ArrayList<>();
    ApiResponses currentLevel = getAnnotation(annotated, ApiResponses.class);
    if (currentLevel != null) {
      results.add(currentLevel);
    }
    if (annotated instanceof Method) {
      ApiResponses parentLevel = findAnnotation(((Method) annotated).getDeclaringClass(), ApiResponses.class);
      if (parentLevel != null) {
        results.add(parentLevel);
      }
    }
    return results;
  }

  public static Function<ApiOperation, ResolvedType> resolvedTypeFromApiOperation(
      final TypeResolver typeResolver,
      final ResolvedType defaultType) {

    return annotation -> getResolvedType(annotation, typeResolver, defaultType);
  }

  public static Function<ApiResponse, ResolvedType> resolvedTypeFromResponse(
      final TypeResolver typeResolver,
      final ResolvedType defaultType) {

    return annotation -> getResolvedType(annotation, typeResolver, defaultType);
  }

  public static Function<
      Operation,
      Collection<io.swagger.v3.oas.annotations.responses.ApiResponse>> fromOperationAnnotation() {

    return annotation -> {
      if (null != annotation) {
        return Arrays.asList(annotation.responses());
      }
      return new ArrayList<>();
    };
  }

  public static Function<
      io.swagger.v3.oas.annotations.responses.ApiResponses,
      Collection<io.swagger.v3.oas.annotations.responses.ApiResponse>> fromApiResponsesAnnotation() {

    return annotation -> {
      if (null != annotation) {
        return Arrays.asList(annotation.value());
      }
      return new ArrayList<>();
    };
  }

  public static Function<Schema, ResolvedType> resolvedTypeFromSchema(
      TypeResolver typeResolver,
      ResolvedType defaultType) {

    return annotation -> getResolvedType(annotation, typeResolver, defaultType);
  }

  @SuppressWarnings("Duplicates")
  private static ResolvedType getResolvedType(
      Schema annotation,
      TypeResolver resolver,
      ResolvedType defaultType) {
    if (null != annotation) {
      Class<?> response = annotation.implementation();
      String responseContainer = annotation.multipleOf() > 0 ? "LIST" : "";
      if (resolvedType(resolver, response, responseContainer).isPresent()) {
        return resolvedType(resolver, response, responseContainer).get();
      }
    }
    return defaultType;
  }

  @SuppressWarnings("Duplicates")
  static ResolvedType getResolvedType(
      ApiOperation annotation,
      TypeResolver resolver,
      ResolvedType defaultType) {

    if (null != annotation) {
      Class<?> response = annotation.response();
      String responseContainer = annotation.responseContainer();
      if (resolvedType(resolver, response, responseContainer).isPresent()) {
        return resolvedType(resolver, response, responseContainer).get();
      }
    }
    return defaultType;
  }

  @SuppressWarnings("Duplicates")
  static ResolvedType getResolvedType(
      ApiResponse annotation,
      TypeResolver resolver,
      ResolvedType defaultType) {

    if (null != annotation) {
      Class<?> response = annotation.response();
      String responseContainer = annotation.responseContainer();
      if (resolvedType(resolver, response, responseContainer).isPresent()) {
        return resolvedType(resolver, response, responseContainer).get();
      }
    }
    return defaultType;
  }

  private static Optional<ResolvedType> resolvedType(
      TypeResolver resolver,
      Class<?> response,
      String responseContainer) {
    if (isNotVoid(response)) {
      if ("List".compareToIgnoreCase(responseContainer) == 0) {
        return of(resolver.resolve(List.class, response));
      } else if ("Set".compareToIgnoreCase(responseContainer) == 0) {
        return of(resolver.resolve(Set.class, response));
      } else {
        return of(resolver.resolve(response));
      }
    }
    return empty();
  }

  private static boolean isNotVoid(Class<?> response) {
    return Void.class != response
        && void.class != response;
  }
}
