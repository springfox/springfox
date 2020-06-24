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

package springfox.documentation.service;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.of;

public class ResolvedMethodParameter {
  private final int parameterIndex;
  private final List<Annotation> annotations;
  private final String defaultName;
  private final ResolvedType parameterType;

  public ResolvedMethodParameter(String paramName, MethodParameter methodParameter, ResolvedType parameterType) {
    this(methodParameter.getParameterIndex(),
        paramName,
        of(methodParameter.getParameterAnnotations()).collect(toList()),
        parameterType);
  }

  public ResolvedMethodParameter(int parameterIndex, String defaultName, List<Annotation> annotations, ResolvedType
      parameterType) {
    this.parameterIndex = parameterIndex;
    this.defaultName = defaultName;
    this.parameterType = parameterType;
    this.annotations = annotations;
  }

  public ResolvedType getParameterType() {
    return parameterType;
  }

  public boolean hasParameterAnnotations() {
    return !annotations.isEmpty();
  }

  public boolean hasParameterAnnotation(Class<? extends Annotation> annotation) {
    return annotations.stream().anyMatch(annotation::isInstance);
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> Optional<T> findAnnotation(final Class<T> annotation) {
    return (Optional<T>) annotations.stream().filter(annotation::isInstance).findFirst();
  }

  public int getParameterIndex() {
    return parameterIndex;
  }

  public Optional<String> defaultName() {
    return ofNullable(defaultName);
  }

  public ResolvedMethodParameter replaceResolvedParameterType(ResolvedType parameterType) {
    return new ResolvedMethodParameter(parameterIndex, defaultName, annotations, parameterType);
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public ResolvedMethodParameter annotate(Annotation annotation) {
    List<Annotation> annotations = new ArrayList<>(this.annotations);
    annotations.add(annotation);
    return new ResolvedMethodParameter(parameterIndex, defaultName, annotations, parameterType);
  }
}
