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

package springfox.documentation.service;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.google.common.collect.Lists.*;

public class ResolvedMethodParameter {
  private final int parameterIndex;
  private final boolean returnType;
  private final List<Annotation> annotations;
  private final String defaultName;
  private final ResolvedType parameterType;

  public ResolvedMethodParameter(String paramName, MethodParameter methodParameter, ResolvedType parameterType) {
    this(methodParameter.getParameterIndex(),
        false,
        paramName,
        newArrayList(methodParameter.getParameterAnnotations()),
        parameterType);
  }
  
  public ResolvedMethodParameter(ResolvedType parameterType) {
    this(0,
        true,
        "",
        newArrayList(new Annotation[0]),
        parameterType);
  }

  private ResolvedMethodParameter(int parameterIndex, boolean returnType, String defaultName, List<Annotation> annotations, ResolvedType
      parameterType) {
    this.parameterIndex = parameterIndex;
    this.returnType = returnType;
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
    return FluentIterable.from(annotations).filter(annotation).size() > 0;
  }

  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return FluentIterable.from(annotations).filter(annotation).first();
  }

  public int getParameterIndex() {
    return parameterIndex;
  }
  
  public boolean isReturnType() {
    return returnType;
  }

  public Optional<String> defaultName() {
    return Optional.fromNullable(defaultName);
  }

  public ResolvedMethodParameter replaceResolvedParameterType(ResolvedType parameterType) {
    return new ResolvedMethodParameter(parameterIndex, returnType, defaultName, annotations, parameterType);
  }

  public ResolvedMethodParameter annotate(Annotation annotation) {
    List<Annotation> annotations = newArrayList(this.annotations);
    annotations.add(annotation);
    return new ResolvedMethodParameter(parameterIndex, returnType, defaultName, annotations, parameterType);
  }
 
  @Override
  public int hashCode() {
    return Objects.hashCode(parameterIndex, returnType, annotations, defaultName, parameterType);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ResolvedMethodParameter that = (ResolvedMethodParameter) o;
    
    return Objects.equal(parameterIndex, that.parameterIndex) &&
        Objects.equal(returnType, that.returnType) &&   
        Objects.equal(annotations, that.annotations) &&
        Objects.equal(defaultName, that.defaultName) &&
        Objects.equal(parameterType, that.parameterType);
  }
}
